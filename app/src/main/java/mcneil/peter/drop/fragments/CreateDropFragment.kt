package mcneil.peter.drop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import mcneil.peter.drop.R

class CreateDropFragment : Fragment(), View.OnClickListener {
    private lateinit var messageBox: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_drop, container, false)

//        messageBox = view.findViewById(R.id.create_drop_text)
//
//        view.findViewById<AppCompatButton>(R.id.create_drop_button).setOnClickListener(this)
//        view.findViewById<View>(R.id.fragment_create_drop).setOnTouchListener(HideKeyboard(activity as Activity))

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
//            R.id.create_drop_button -> dropMessage()
        }
    }

//    private fun dropMessage() {
//        if (auth.currentUser != null) {
//            val dropMessage = messageBox.text.toString()
//            val title = create_drop_title.text.toString()
//            if (dropMessage.isBlank() || title.isBlank()) {
//                Snackbar.make(activity!!.findViewById(R.id.fragment_create_drop), "Text must not be blank", Snackbar.LENGTH_LONG).show()
//                messageBox.setBackgroundColor(Color.RED)
//            } else {
//                val dropLocation = DropLocation(locationUtil.lastKnownLocation.latitude, locationUtil.lastKnownLocation.longitude)
//                val drop = Drop(title=title, message = dropMessage, location = dropLocation, ownerId = auth.currentUser!!.uid, createdOn = getCurrentDateTime())
//
//                val id = firebaseUtil.writeDrop(drop)
//
//                firebaseUtil.linkDropToLocation(id, locationUtil.lastKnownLocation)
//
//                Log.d(tag, "Message: $dropMessage\n Location: ${locationUtil.lastKnownLocation}")
//
//                //TODO - show a confirmation of some sort
//            }
//        } else {
//            Log.d(tag, "User is not logged in so cannot create a drop")
//        }
//    }
}