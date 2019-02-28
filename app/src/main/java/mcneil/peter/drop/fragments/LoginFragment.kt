package mcneil.peter.drop.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.android.synthetic.main.fragment_login.*
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.R
import mcneil.peter.drop.util.HideKeyboard
import mcneil.peter.drop.util.Validate

class LoginFragment : DialogFragment(), View.OnClickListener {
    private lateinit var loginButtons: Group
    private lateinit var signedInButtons: Group
    private lateinit var userInfo: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        view.findViewById<AppCompatButton>(R.id.emailSignInButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.emailCreateAccountButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.signOutButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.verifyEmailButton).setOnClickListener(this)
        view.findViewById<AppCompatImageButton>(R.id.closeLogin).setOnClickListener(this)

        loginButtons = view.findViewById(R.id.loginInButtons)
        signedInButtons = view.findViewById(R.id.signedInButtons)
        userInfo = view.findViewById(R.id.userInformation)

        view.findViewById<View>(R.id.frag_login_layout).setOnTouchListener(HideKeyboard(activity as Activity))

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DropTheme)
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.emailCreateAccountButton -> createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.emailSignInButton -> signIn(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.signOutButton -> signOut()
            R.id.verifyEmailButton -> sendEmailVerification()
            R.id.closeLogin -> dismiss()
        }
    }

    private fun createAccount(email: String, password: String) {
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(tag, "createUserWithEmail:success")
                updateUI()
            } else {
                Log.w(tag, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                updateUI()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(tag, "signInWithEmail:success")
                updateUI()
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, getString(R.string.toast_password_wrong), Toast.LENGTH_SHORT).show()
                    Log.w(tag, "signInWithEmail:failure:passwordWrong")
                } else {
                    Toast.makeText(context, getString(R.string.toast_auth_failed), Toast.LENGTH_SHORT).show()
                    Log.w(tag, "signInWithEmail:failure:unknown", task.exception)
                }
                updateUI()
            }
        }
    }

    private fun updateUI() {
        val user = auth.currentUser
        if (user != null) {
            userInfo.text = getString(R.string.emailpassword_status_fmt, user.email, user.isEmailVerified)

            loginButtons.visibility = View.GONE
            signedInButtons.visibility = View.VISIBLE

            verifyEmailButton.isEnabled = !user.isEmailVerified
        } else {
            userInfo.text = null

            loginButtons.visibility = View.VISIBLE
            signedInButtons.visibility = View.GONE
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI()
    }

    private fun sendEmailVerification() {
        verifyEmailButton.isEnabled = false

        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(context as Activity) { task ->
            verifyEmailButton.isEnabled = true
            if (task.isSuccessful) {
                Toast.makeText(context, getString(R.string.toast_email_sent), Toast.LENGTH_SHORT).show()
            } else {
                Log.e(tag, "sendEmailVerification", task.exception)
                Toast.makeText(context, getString(R.string.toast_email_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

}