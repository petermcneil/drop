package mcneil.peter.drop.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_find_search.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.R
import mcneil.peter.drop.listeners.AddDropToUserListener

class FindSearchActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val EXPLORATION_NOT = "EXPLORATION"
        const val EXPLORATION_ID = 1337
    }

    private lateinit var explorationNot: NotificationCompat.Builder
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_find_search)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_search)

        createNotificationChannel()
        explorationNot = NotificationCompat.Builder(this, EXPLORATION_NOT)
            .setSmallIcon(R.drawable.drop_logo)
            .setContentTitle(getString(R.string.notification_explore_title))
            .setContentText(getString(R.string.notification_explore_content))
            .setAutoCancel(false)
            .setOngoing(true)

        find_explore_btn.setOnClickListener(this)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.search_notification_name)
            val descriptionText = getString(R.string.search_notification_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(EXPLORATION_NOT, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.find_explore_btn -> {
                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(EXPLORATION_ID, explorationNot.build())
                }

                //ADD FOUND DROP
                DropApp.firebaseUtil.addFoundDropToUser(AddDropToUserListener("-Ldik90_02zH0r_DQImK"))
            }
        }
    }

}