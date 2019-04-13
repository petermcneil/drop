package mcneil.peter.drop.activities.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_email_verification.*
import kotlinx.android.synthetic.main.activity_login.input_email
import kotlinx.android.synthetic.main.activity_login.input_password
import kotlinx.android.synthetic.main.activity_signup.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.MainActivity
import mcneil.peter.drop.util.Validate
import org.jetbrains.anko.doAsync

class CreateAccountActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var progress: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        progress = AlertDialog.Builder(this).setView(R.layout.alert_progress).create()
        a_signup_button.setOnClickListener(this)
        Log.d(TAG, "CreateAccountActivity created")
    }

    override fun onBackPressed() {
        Log.d(TAG, "Back button disabled")
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.a_signup_button -> createAccount()
        }
    }

    private fun createAccount() {
        Log.d(TAG, "Creating account")
        if (!Validate.emailPasswordForm(input_email.text.toString(), input_password.text.toString())) {
            Log.d(TAG, "Invalid email or password")
            return
        }
        progress.show()
        DropApp.auth.createUserWithEmailAndPassword(input_email.text.toString(), input_password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                    addUserName()
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserName() {
        val user = DropApp.auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder().setDisplayName(input_name.text.toString()).build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "addUserName:success")
                sendEmailVerification()
            } else {
                Log.w(TAG, "addUserName:failure", task.exception)
                Toast.makeText(this, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmailVerification() {
        val user = DropApp.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(this) { task ->
            progress.dismiss()
            if (task.isSuccessful) {
                Toast.makeText(this, getString(R.string.toast_email_sent, auth.currentUser!!.email), Toast.LENGTH_SHORT)
                    .show()
                setContentView(R.layout.activity_email_verification)
                a_ev_email_text.text = getString(R.string.verification_email_sent_to, auth.currentUser!!.email)
                doAsync {
                    var verified = false
                    while (!verified) {
                        Thread.sleep(500)
                        auth.currentUser?.reload()
                        val reloadedUser = auth.currentUser
                        if (reloadedUser != null && reloadedUser.isEmailVerified) {
                            Log.d(TAG, "Email is verified")
                            verified = true
                            Toast.makeText(this@CreateAccountActivity, getString(R.string.verify_email), Toast.LENGTH_LONG).show()
                            val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            ContextCompat.startActivity(this@CreateAccountActivity, intent, null)
                        }

                    }
                }
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(this, getString(R.string.toast_email_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }
}