package mcneil.peter.drop.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import mcneil.peter.drop.R

class MainFragment : Fragment(), View.OnClickListener {
    private lateinit var fm: FragmentManager
    private lateinit var myContext: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        view.findViewById<AppCompatButton>(R.id.main_drop_button).setOnClickListener(this)
        fm = myContext.supportFragmentManager
        return view
    }

    override fun onAttach(context: Context?) {
        myContext = activity as FragmentActivity
        super.onAttach(context)
    }

    ///////////////////////// View.OnClickListener overrides /////////////////////////
    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_drop_button -> {
                fm.beginTransaction().replace(R.id.activity_main, CreateDropFragment()).addToBackStack("Create Drop").commit()
            }
        }
    }
}