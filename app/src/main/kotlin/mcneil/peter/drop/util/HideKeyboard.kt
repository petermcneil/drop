package mcneil.peter.drop.util

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

class HideKeyboard constructor(act: Activity) : View.OnTouchListener {
    private val activity = act

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
        v.performClick()
        return true
    }
}