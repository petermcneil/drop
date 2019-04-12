package mcneil.peter.drop.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.activity_login.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.R
import mcneil.peter.drop.util.Validate

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var loginButtons: Group
    private lateinit var signedInButtons: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailSignInButton.setOnClickListener(this)
        emailCreateAccountButton.setOnClickListener(this)
        signOutButton.setOnClickListener(this)
        verifyEmailButton.setOnClickListener(this)
        closeLogin.setOnClickListener(this)

        loginButtons = findViewById(R.id.loginInButtons)
        signedInButtons = findViewById(R.id.signedInButtons)
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        val sp = getSharedPreferences("LoginActivity", Context.MODE_PRIVATE)
        sp.edit().putBoolean("active", true).apply()
    }

    override fun onStop() {
        super.onStop()
        val sp = getSharedPreferences("LoginActivity", Context.MODE_PRIVATE)
        sp.edit().putBoolean("active", false).apply()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.emailCreateAccountButton -> createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.emailSignInButton -> signIn(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.signOutButton -> signOut()
            R.id.verifyEmailButton -> sendEmailVerification()
            R.id.closeLogin -> finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
            return
        }
        DropApp.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "createUserWithEmail:success")
                updateUI()
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(this, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                updateUI()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
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
                } else {
                    Toast.makeText(this, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "signInWithEmail:failure:unknown", task.exception)
                }
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val user = DropApp.auth.currentUser
        if (user != null) {
            userInformation.text = getString(R.string.emailpassword_status_fmt, user.email, user.isEmailVerified)

            loginButtons.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE

            verifyEmailButton.isEnabled = !user.isEmailVerified
        } else {
            userInformation.text = null

            loginButtons.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }

    private fun signOut() {
        DropApp.auth.signOut()
        updateUI()
    }

    private fun sendEmailVerification() {
        verifyEmailButton.isEnabled = false

        val user = DropApp.auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(this as Activity) { task ->
            verifyEmailButton.isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(this, getString(R.string.toast_email_sent), Toast.LENGTH_SHORT).show()
            } else {
                Log.e(TAG, "sendEmailVerification", task.exception)
                Toast.makeText(this, getString(R.string.toast_email_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

}