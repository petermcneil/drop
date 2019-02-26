package mcneil.peter.drop.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.DropLocation
import mcneil.peter.drop.util.HideKeyboard

class CreateDropFragment : DialogFragment(), View.OnClickListener {
    private val TAG = this.javaClass.canonicalName

    private lateinit var messageBox: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_drop, container)

        view.findViewById<AppCompatButton>(R.id.create_drop_button).setOnClickListener(this)
        messageBox = view.findViewById(R.id.create_drop_text)

        view.findViewById<View>(R.id.fragment_create_drop).setOnTouchListener(HideKeyboard(activity as Activity))

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DropTheme)
    }

    override fun onResume() {
        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
        super.onResume()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.create_drop_button -> dropMessage()
        }
    }

    private fun dropMessage() {
        val dropMessage = messageBox.text.toString()
        val dropLocation = DropLocation(locationUtil.lastKnownLocation.latitude, locationUtil.lastKnownLocation.longitude)
        val drop = Drop(dropMessage, dropLocation, auth.currentUser!!.uid)

        val id = firebaseUtil.writeDrop(drop)

        firebaseUtil.linkDropToLocation(id, locationUtil.lastKnownLocation)

        Log.d(TAG, "Message: $dropMessage\n Location: ${locationUtil.lastKnownLocation}")
        dismiss()
    }
}