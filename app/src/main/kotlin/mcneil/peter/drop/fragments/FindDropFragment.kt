package mcneil.peter.drop.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.FindCompassActivity
import mcneil.peter.drop.activities.FindSearchActivity

class FindDropFragment : Fragment(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private lateinit var fm: FragmentManager
    private lateinit var con: FragmentActivity

    private var active = false
    private lateinit var activeFrag: Fragment

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_find_main, container, false)
        fm = con.supportFragmentManager
        view.findViewById<AppCompatButton>(R.id.f_dm_search).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.f_dm_compass).setOnClickListener(this)

        activeFrag = this
        return view
    }

    override fun onAttach(context: Context?) {
        con = activity as FragmentActivity
        super.onAttach(context)
    }

    override fun onClick(v: View?) {
        Log.d(TAG, "Clicked something: ${v?.id}")
        when (v!!.id) {
            R.id.f_dm_search -> {
                Log.d(TAG, "Showing search activity")
                Toast.makeText(context, "Showing search", Toast.LENGTH_LONG).show()
                active = true

                val intent = Intent(context, FindSearchActivity::class.java)
                startActivity(intent)
            }
            R.id.f_dm_compass -> {
                Log.d(TAG, "Showing compass activity")
                Toast.makeText(context, "Showing compass", Toast.LENGTH_LONG).show()
                active = true

                val intent = Intent(context, FindCompassActivity::class.java)
                startActivity(intent)
            }
        }
    }


}