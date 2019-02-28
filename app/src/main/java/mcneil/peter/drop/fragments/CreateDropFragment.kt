package mcneil.peter.drop.fragments

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.DropLocation
import mcneil.peter.drop.util.HideKeyboard

class CreateDropFragment : DialogFragment(), View.OnClickListener {
    private lateinit var messageBox: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_drop, container, false)

        messageBox = view.findViewById(R.id.create_drop_text)

        view.findViewById<AppCompatButton>(R.id.create_drop_button).setOnClickListener(this)
        view.findViewById<View>(R.id.fragment_create_drop).setOnTouchListener(HideKeyboard(activity as Activity))

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DropTheme)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.create_drop_button -> dropMessage()
        }
    }

    private fun dropMessage() {
        if (auth.currentUser != null) {
            val dropMessage = messageBox.text.toString()
            if (dropMessage.isBlank()) {
                Snackbar.make(activity!!.findViewById(R.id.fragment_create_drop), "Text must not be blank", Snackbar.LENGTH_LONG).show()
                messageBox.setBackgroundColor(Color.RED)
            } else {
                val dropLocation = DropLocation(locationUtil.lastKnownLocation.latitude, locationUtil.lastKnownLocation.longitude)
                val drop = Drop(dropMessage, dropLocation, auth.currentUser!!.uid)

                val id = firebaseUtil.writeDrop(drop)

                firebaseUtil.linkDropToLocation(id, locationUtil.lastKnownLocation)

                Log.d(tag, "Message: $dropMessage\n Location: ${locationUtil.lastKnownLocation}")
                dismiss()
            }
        } else {
            Log.d(tag, "User is not logged in so cannot create a drop")
        }
    }
}