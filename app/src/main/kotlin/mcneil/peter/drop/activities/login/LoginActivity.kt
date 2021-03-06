package mcneil.peter.drop.activities.login

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_login.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.MainActivity
import mcneil.peter.drop.util.Validate

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var resetPasswordDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        a_login_create.setOnClickListener(this)
        a_login_button.setOnClickListener(this)
        a_forgot_button.setOnClickListener(this)

        resetPasswordDialog = AlertDialog.Builder(this)
            .setPositiveButton("Reset") { _, _ ->
                run {
                    val email = input_email.text.toString()
                    Log.d(TAG, "passwordDialog: Email $email")
                    DropApp.auth.sendPasswordResetEmail(email)
                    Toast.makeText(this, getString(R.string.toast_password_reset, email), Toast.LENGTH_SHORT).show()
                }
            }
            .setMessage(getString(R.string.reset_pass_email, input_email.text.toString())).create()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.a_login_create -> openSignUp()
            R.id.a_login_button -> signIn(input_email.text.toString(), input_password.text.toString())
            R.id.a_forgot_button -> resetPassword()
        }
    }

    private fun openSignUp() {
        startActivity(Intent(this, CreateAccountActivity::class.java))
    }

    private fun signIn(email: String, password: String) {
        if (!Validate.emailPasswordForm(input_email.text.toString(), input_password.text.toString())) {
            return
        }

        DropApp.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                updateUI()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, getString(R.string.toast_password_wrong), Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithEmail:failure:passwordWrong")
                    a_forgot_button.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithEmail:failure:unknown", task.exception)
                }
            }
        }
    }

    private fun resetPassword() {
        Log.d(TAG, "resetPassword: Showing dialog")
        resetPasswordDialog.setMessage(getString(R.string.reset_pass_email, input_email.text.toString()))
        resetPasswordDialog.show()
    }

    private fun updateUI() {
        val user = DropApp.auth.currentUser
        if (user != null) {
            Log.d(TAG, "updateUI: User is logged in")
            PreferenceManager.getDefaultSharedPreferences(this).edit().apply {
                Log.d(TAG, "updateUI: Logged in has happened")
                putBoolean(DropApp.LOGGED_IN_PREF, true)
                apply()
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            ContextCompat.startActivity(this, intent, null)
        }
    }

}