package mcneil.peter.drop

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import mcneil.peter.drop.activities.ExploreFoundActivity
import mcneil.peter.drop.activities.FindExploreActivity
import mcneil.peter.drop.model.Drop

class DropTransitionIntentService : IntentService("Drop") {
    private val TAG = this.javaClass.simpleName

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: Called")
        val event = GeofencingEvent.fromIntent(intent)
        if (event.hasError()) {
            Log.e(TAG, event.toString())
            return
        }

        val trans = event.geofenceTransition

        if (trans == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, "onHandleIntent: Transition was enter")

            val triggers = event.triggeringGeofences
            Log.d(TAG, "onHandleIntent: Grabbing first drop geofence id")
            val dropId = triggers.first().requestId
            sendNotification(dropId)
        } else {
            Log.d(TAG, "onHandleIntent: Transition not recognised $trans")
        }
    }

    private fun sendNotification(dropId: String) {
        val drop = DropApp.exploreDrops[dropId]

        if (drop != null) {
            //Build an intent to launch the found drop activity
            val notifyIntent = Intent(this, ExploreFoundActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            notifyIntent.putExtra("drop", Drop())

            val notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(this, FindExploreActivity.FOUND_NOT).apply {
                setContentIntent(notifyPendingIntent)
                setSmallIcon(R.drawable.drop_logo)
                setContentText(getString(R.string.notification_found_drop))
                setAutoCancel(true)

            }

            Log.d(TAG, "sendNotification: Sending")
            with(NotificationManagerCompat.from(this)) {
                notify(FindExploreActivity.FOUND_ID, builder.build())
            }
        } else {
            Log.e(TAG, "sendNotification: Drop was null for some reason")
        }
    }
}
