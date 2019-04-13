package mcneil.peter.drop.listener

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.activities.MainActivity

class VerifiedEmailListener(private val context: Context) : FirebaseAuth.AuthStateListener {
    private val TAG = this.javaClass.simpleName

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        auth.currentUser!!.reload()
        Log.d(TAG, "Triggered")
        val user = auth.currentUser
        if (user != null && user.isEmailVerified) {
            Log.d(TAG, "Email is verified")
            DropApp.auth.removeAuthStateListener(this)
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(context, intent, null)
        }
    }

}