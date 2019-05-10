package mcneil.peter.drop.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.snackbar.Snackbar
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.FindExploreActivity
import mcneil.peter.drop.activities.FindHuntActivity

class FindDropFragment : Fragment(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var fm: FragmentManager
    private lateinit var con: FragmentActivity
    private lateinit var exploreSnack: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_find_main, container, false)
        fm = con.supportFragmentManager
        view.findViewById<AppCompatButton>(R.id.f_dm_search).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.f_dm_compass).setOnClickListener(this)

        exploreSnack = Snackbar.make(view.findViewById<LinearLayoutCompat>(R.id.button_container), "Explore hasn't been implemented yet", Snackbar.LENGTH_LONG)
        return view
    }

    override fun onAttach(context: Context?) {
        con = activity as FragmentActivity
        super.onAttach(context)
    }

    override fun onClick(v: View) {
        Log.d(TAG, "Clicked something: ${v.id}")
        when (v.id) {
            R.id.f_dm_search -> {
                Log.d(TAG, "Showing search activity")
                val intent = Intent(context, FindExploreActivity::class.java)
                startActivity(intent)
            }
            R.id.f_dm_compass -> {
                Log.d(TAG, "Showing compass activity")
                val intent = Intent(context, FindHuntActivity::class.java)
                startActivity(intent)
            }
        }
    }
}