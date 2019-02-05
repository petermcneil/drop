package mcneil.peter.drop.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_login.*
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.BaseActivity
import mcneil.peter.drop.util.HideKeyboard
import mcneil.peter.drop.util.Validate

class LoginFragment : DialogFragment(), View.OnClickListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButtons: Group
    private lateinit var signedInButtons: Group
    private lateinit var userInfo: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container)
        auth = FirebaseAuth.getInstance()

        //Register buttons
        view.findViewById<AppCompatButton>(R.id.emailSignInButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.emailCreateAccountButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.signOutButton).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.verifyEmailButton).setOnClickListener(this)
        view.findViewById<AppCompatImageButton>(R.id.closeLogin).setOnClickListener(this)

        loginButtons = view.findViewById(R.id.loginInButtons)
        signedInButtons = view.findViewById(R.id.signedInButtons)
        userInfo = view.findViewById(R.id.userInformation)

        //Hide Keyboard
        view.findViewById<View>(R.id.frag_login_layout).setOnTouchListener(HideKeyboard(activity as Activity))

        Log.d(BaseActivity.TAG, "Completed setting up login page")

        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        val params = dialog.window!!.attributes
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        super.onResume()
    }


    private fun createAccount(email: String, password: String) {
        Log.d(BaseActivity.TAG, "createAccount:$email")
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
            return
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(BaseActivity.TAG, "createUserWithEmail:success")
                val user = auth.currentUser
                updateUI(user)
            } else {
                Log.w(BaseActivity.TAG, "createUserWithEmail:failure", task.exception)
                Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }
    }

    private fun signIn(email: String, password: String) {
        Log.d(BaseActivity.TAG, "signIn:$email")
        if (!Validate.emailPasswordForm(fieldEmail, fieldPassword)) {
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(context as Activity) { task ->
            if (task.isSuccessful) {
                Log.d(BaseActivity.TAG, "signInWithEmail:success")
                val user = auth.currentUser
                updateUI(user)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "Password is wrong", Toast.LENGTH_SHORT).show()
                    Log.w(BaseActivity.TAG, "signInWithEmail:failure:passwordWrong")
                } else {
                    Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    Log.w(BaseActivity.TAG, "signInWithEmail:failure:unknown", task.exception)
                }
                updateUI(null)
            }

            if (!task.isSuccessful) {

            }
        }
    }

    private fun updateUI(user: FirebaseUser?) {
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


    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.emailCreateAccountButton -> createAccount(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.emailSignInButton -> signIn(fieldEmail.text.toString(), fieldPassword.text.toString())
            R.id.signOutButton -> signOut()
            R.id.verifyEmailButton -> sendEmailVerification()
            R.id.closeLogin -> dismiss()
        }
    }

    private fun signOut() {
        auth.signOut()
        updateUI(null)
    }

    private fun sendEmailVerification() {
        verifyEmailButton.isEnabled = false

        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener(context as Activity) { task ->

            verifyEmailButton.isEnabled = true

            if (task.isSuccessful) {
                Toast.makeText(context, "Verification email sent to ${user.email} ", Toast.LENGTH_SHORT).show()
            } else {
                Log.e(BaseActivity.TAG, "sendEmailVerification", task.exception)
                Toast.makeText(context, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }
}