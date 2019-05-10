package mcneil.peter.drop.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_find_explore.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.DropTransitionIntentService
import mcneil.peter.drop.R
import mcneil.peter.drop.model.ACallback
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.Either
import org.jetbrains.anko.doAsync

class FindExploreActivity : AppCompatActivity(), View.OnClickListener, ACallback<Pair<String, Drop>> {
    private val TAG = this.javaClass.simpleName

    companion object {
        const val EXPLORATION_NOT = "EXPLORATION"
        const val FOUND_NOT = "FOUND_NOT"
        const val EXPLORATION_ID = 1337
        const val FOUND_ID = 1337
        const val GEOFENCE_RADIUS_IN_METERS = 0.1f
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS = 1000000L
    }

    private lateinit var explorationNot: NotificationCompat.Builder
    private lateinit var geofencingClient: GeofencingClient

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_find_explore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_explore)

        createNotificationChannel()
        explorationNot = NotificationCompat.Builder(this, EXPLORATION_NOT)
            .setSmallIcon(R.drawable.drop_logo)
            .setContentTitle(getString(R.string.notification_explore_title))
            .setContentText(getString(R.string.notification_explore_content))
            .setAutoCancel(false)
            .setOngoing(true)

        find_explore_btn.setOnClickListener(this)
        cancel_explore.setOnClickListener(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Exploration notification channels
            val name = getString(R.string.search_notification_name)
            val descriptionText = getString(R.string.search_notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(EXPLORATION_NOT, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            //Found notification channel
            val foundName = "Found Drop"
            val foundDesc = "Displayed when a drop is found in exploration mode"
            val foundChannel = NotificationChannel(FOUND_NOT, foundName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = foundDesc
            }
            notificationManager.createNotificationChannel(foundChannel)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_explore_btn -> {
                Log.d(TAG, "onClick: Find explore button clicked")
                find_explore_btn.visibility = View.GONE
                cancel_explore.visibility = View.VISIBLE
                with(NotificationManagerCompat.from(this)) {
                    notify(EXPLORATION_ID, explorationNot.build())
                }
                Log.d(TAG, "onClick: notification displayed")
                DropApp.exploreDrops.clear()
                Log.d(TAG, "onClick: explore drops cleared")

                val location = locationUtil.getLastKnownLocation()

                //Deal with uninstantiated last location
                if (location is Either.Right) {
                    Log.d(TAG, "onClick: notification displayed")
                    firebaseUtil.findNewDrop(location = location.value, callback = this, radius = 10.00)
                } else {
                    locationUtil.updateLastKnownLocation()

                    val timeBefore = System.currentTimeMillis()
                    doAsync {
                        var timeNow = System.currentTimeMillis()
                        while ((timeNow - timeBefore) < 10000) {
                            timeNow = System.currentTimeMillis()
                        }
                        val l = locationUtil.getLastKnownLocation()
                        if (l is Either.Right) {
                            firebaseUtil.findNewDrop(location = l.value, callback = this@FindExploreActivity, radius = 10.00)
                        } else {
                            Log.e(TAG, "location has not been found")
                        }
                    }
                }
            }
            R.id.cancel_explore -> {
                find_explore_btn.visibility = View.VISIBLE
                cancel_explore.visibility = View.GONE
                with(NotificationManagerCompat.from(this)) {
                    cancel(EXPLORATION_ID)
                }
                DropApp.exploreDrops.clear()

            }
        }
    }

    override fun callback(ret: Pair<String, Drop>) {
        Log.d(TAG, "callback: Creating geofence for drop - ${ret.first}")
        val geofence = Geofence.Builder().setRequestId(ret.first)
            .setCircularRegion(ret.second.location.latitude, ret.second.location.longitude, GEOFENCE_RADIUS_IN_METERS)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER).build()

        DropApp.exploreDrops[ret.first] = ret.second
        val request = GeofencingRequest.Builder().addGeofence(geofence)
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER).build()
        try {
            Log.d(TAG, "callback: Adding geofence to client at location ${ret.second.location.latitude} | ${ret.second.location.longitude}")
            geofencingClient.addGeofences(request, geofencePendingIntent)
        } catch (se: SecurityException) {
            Log.e(TAG, "callback: Location permission denied")
        }
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, DropTransitionIntentService::class.java)
        PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}