package mcneil.peter.drop.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_email_verification.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.MainActivity
import org.jetbrains.anko.doAsync

class EmailVerificationActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private var resendCalled: Long = System.currentTimeMillis() % 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)
        a_ev_email_text.text = getString(R.string.verification_email_sent_to, DropApp.auth.currentUser!!.email)

        a_ev_email_not_arrived.setOnClickListener(this)
        doAsync {
            var verified = false
            while (!verified) {
                Thread.sleep(500)
                DropApp.auth.currentUser?.reload()
                val reloadedUser = DropApp.auth.currentUser
                if (reloadedUser != null && reloadedUser.isEmailVerified) {
                    Log.d(TAG, "Email is verified")
                    verified = true
                    Toast.makeText(this@EmailVerificationActivity, getString(R.string.verify_email), Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(this@EmailVerificationActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    ContextCompat.startActivity(this@EmailVerificationActivity, intent, null)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.a_ev_email_not_arrived -> {
                val timeNow = System.currentTimeMillis() % 1000
                val diff = timeNow - resendCalled
                if (diff > 20000) {
                    Toast.makeText(this@EmailVerificationActivity, getString(R.string.resend_email), Toast.LENGTH_LONG)
                        .show()
                    auth.currentUser!!.sendEmailVerification()
                    resendCalled = timeNow
                } else {
                    Toast.makeText(this@EmailVerificationActivity, getString(R.string.cannot_resend), Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

}