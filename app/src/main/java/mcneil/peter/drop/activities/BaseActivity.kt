package mcneil.peter.drop.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import mcneil.peter.drop.util.HideKeyboard


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    companion object {
        val TAG: String = "PM6"
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    /**
     *  Hides keyboard when not focused
     *
     *  @param id of main layout
     *  @param activity on which to close the keyboard
     *  @return closed keyboard
     */
    fun hideKeyboard(id: Int) {
        findViewById<View>(id).setOnTouchListener(HideKeyboard(this))
    }

}