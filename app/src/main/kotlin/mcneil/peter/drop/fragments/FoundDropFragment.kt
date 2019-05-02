package mcneil.peter.drop.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.FindCompassActivity
import mcneil.peter.drop.activities.MainActivity
import mcneil.peter.drop.listeners.AddDropToUserListener
import mcneil.peter.drop.model.Drop

class FoundDropFragment : DialogFragment(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    companion object {
        fun newInstance(drop: Drop, dropId: String): FoundDropFragment {
            val frag = FoundDropFragment()
            frag.drop = drop
            frag.dropId = dropId
            return frag
        }
    }

    private lateinit var drop: Drop
    private lateinit var dropId: String
    private lateinit var message: TextView
    private lateinit var title: TextView
    private lateinit var dateCreated: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_found_drop, container, false)

        message = view.findViewById(R.id.f_d_message)
        title = view.findViewById(R.id.drop_title)
        dateCreated = view.findViewById(R.id.f_d_date)

        message.text = drop.message
        title.text = drop.title
        dateCreated.text = drop.formattedDate()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<AppCompatButton>(R.id.keep_it).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.leave_it).setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.leave_it -> {
                val intent = Intent(context, FindCompassActivity::class.java)
                startActivity(intent)
                dismiss()
            }
            R.id.keep_it -> {
                Log.d(TAG, "keepIt: Adding drop to user and redirecting to MainActivity")
                firebaseUtil.addFoundDropToUser(AddDropToUserListener(dropId))
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                dismiss()
            }
        }
    }

}