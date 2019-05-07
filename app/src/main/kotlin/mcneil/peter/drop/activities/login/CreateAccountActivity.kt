package mcneil.peter.drop.activities.login

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_login.input_email
import kotlinx.android.synthetic.main.activity_login.input_password
import kotlinx.android.synthetic.main.activity_signup.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.R
import mcneil.peter.drop.util.Validate

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
                PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                    Log.d(TAG, "Logged in has happened")
                    putBoolean(DropApp.LOGGED_IN_PREF, true)
                    apply()
                }
                Toast.makeText(this, getString(R.string.toast_email_sent, auth.currentUser!!.email), Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, EmailVerificationActivity::class.java))
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(this, getString(R.string.toast_email_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }
}