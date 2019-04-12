package mcneil.peter.drop.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.LoginActivity
import mcneil.peter.drop.activities.SettingsActivity
import mcneil.peter.drop.adapter.FeedAdapter
import mcneil.peter.drop.model.Drop

class MainFragment : Fragment(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    private lateinit var fm: FragmentManager
    private lateinit var con: FragmentActivity
    private lateinit var recycleView: RecyclerView
    private lateinit var welcome: TextView
    private val dataset: MutableList<Drop?> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recycleView = view.findViewById(R.id.main_view)

        recycleView.adapter = FeedAdapter(dataset)
        recycleView.layoutManager = LinearLayoutManager(activity)

        fm = con.supportFragmentManager
        view.findViewById<AppCompatImageView>(R.id.f_m_settings).setOnClickListener(this)

        welcome = view.findViewById<TextView>(R.id.f_m_welcome)
        val name = auth.currentUser!!.displayName
        val message = if(name == null) {
            "$name!"
        } else {
            "!"
        }
        welcome.text = resources.getString(R.string.f_m_welcome, message)
        return view
    }

    override fun onAttach(context: Context?) {
        con = activity as FragmentActivity
        super.onAttach(context)
    }

    ///////////////////////// View.OnClickListener overrides /////////////////////////
    override fun onClick(v: View) {
        when (v.id) {
            R.id.f_m_settings -> openSettings()
        }
    }

    private fun openSettings() {
        Log.i(TAG, "Opening settings")
        startActivity(Intent(context, SettingsActivity::class.java))
    }

    private fun signOut() {
        Log.i(TAG, "Signing out")
        DropApp.auth.signOut()
        startActivity(Intent(context, LoginActivity::class.java))
    }
}