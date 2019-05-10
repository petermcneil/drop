package mcneil.peter.drop.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.R
import mcneil.peter.drop.listeners.AddDropToUserListener
import mcneil.peter.drop.model.Drop

class ExploreFoundActivity : BaseActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    private lateinit var drop: Drop
    private lateinit var dropId: String
    private lateinit var message: TextView
    private lateinit var title: TextView
    private lateinit var dateCreated: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found_drop)

        drop = intent.getSerializableExtra("drop") as Drop

        message = findViewById(R.id.f_d_message)
        title = findViewById(R.id.drop_title)
        dateCreated = findViewById(R.id.f_d_date)

        message.text = drop.message
        title.text = drop.title
        dateCreated.text = drop.formattedDate()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.leave_it -> {
                val intent = Intent(this, FindExploreActivity::class.java)
                startActivity(intent)
            }
            R.id.keep_it -> {
                Log.d(TAG, "keepIt: Adding drop to user and redirecting to MainActivity")
                DropApp.firebaseUtil.addFoundDropToUser(AddDropToUserListener(dropId))
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }
}