package mcneil.peter.drop.activities

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.login.EmailVerificationActivity
import mcneil.peter.drop.activities.login.LoginActivity
import mcneil.peter.drop.fragments.CreateDropFragment
import mcneil.peter.drop.fragments.FindDropFragment
import mcneil.peter.drop.fragments.MainFragment
import pub.devrel.easypermissions.AfterPermissionGranted

class MainActivity : BaseActivity() {
    private val TAG = this.javaClass.simpleName
    private val fm = supportFragmentManager

    private val createDrop = CreateDropFragment()
    private val mainFragment = MainFragment()
    private val findDrop = FindDropFragment()
    private var active: Fragment = mainFragment
    private var loggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loggedIn = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DropApp.LOGGED_IN_PREF, false)

        if (loggedIn) {
            Log.d(TAG, "onCreate: Creating MainActivity")

            bottom_navigation.setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.menu_create -> replaceFrag(createDrop)
                    R.id.menu_main -> replaceFrag(mainFragment)
                    R.id.menu_find -> replaceFrag(findDrop)
                    else -> replaceFrag(createDrop)
                }
                return@setOnNavigationItemSelectedListener true
            }

            fm.beginTransaction().add(R.id.main_frag_container, findDrop, "3").hide(findDrop).commit()
            fm.beginTransaction().add(R.id.main_frag_container, createDrop, "2").hide(createDrop).commit()
            fm.beginTransaction().add(R.id.main_frag_container, mainFragment, "1").commit()

            Log.d(TAG, "onCreate: Created bottom navigation bar and fragments")
        } else {
            Log.d(TAG, "onCreate: Not logged in")
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        locationUtil.removeLocationUpdates()
    }

    private fun replaceFrag(frag: Fragment) {
        Log.d(TAG, "replaceFrag: Replacing frag ${active.javaClass.simpleName} with ${frag.javaClass.simpleName}")
        fm.beginTransaction().addToBackStack(frag.tag).hide(active).show(frag).addToBackStack(null).commit()
        active = frag
    }

    @AfterPermissionGranted(LOCATION)
    private fun updateUI() {
        Log.d(TAG, "updateUI: Called")
        val user = auth.currentUser
        if (user != null) {
            user.reload()
            if (!user.isEmailVerified) {
                Log.d(TAG, "updateUI: Email not verified")
                Toast.makeText(this, "Email is not verified, check your emails", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, EmailVerificationActivity::class.java))
            } else {
                Log.d(TAG, "updateUI: Requesting to update last known location")
                locationUtil.updateLastKnownLocation()
            }
        }
    }

}