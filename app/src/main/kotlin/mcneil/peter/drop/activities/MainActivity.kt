package mcneil.peter.drop.activities

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.login.EmailVerificationActivity
import mcneil.peter.drop.activities.login.LoginActivity
import mcneil.peter.drop.fragments.CreateDropFragment
import mcneil.peter.drop.fragments.FindDropFragment
import mcneil.peter.drop.fragments.MainFragment
import mcneil.peter.drop.util.LocationUtil
import pub.devrel.easypermissions.AfterPermissionGranted

class MainActivity : BaseActivity() {
    private val TAG = this.javaClass.simpleName
    private val fm = supportFragmentManager

    private val createDrop = CreateDropFragment()
    private val mainFragment = MainFragment()
    private val findDrop = FindDropFragment()
    private var active: Fragment = mainFragment

    ///////////////////////// Activity overrides /////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationUtil = LocationUtil(this.getSystemService(Context.LOCATION_SERVICE) as LocationManager, FusedLocationProviderClient(this))

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_create -> {
                    replaceFrag(createDrop)
                }
                R.id.menu_main -> {
                    replaceFrag(mainFragment)
                }
                R.id.menu_find -> {
                    replaceFrag(findDrop)
                }
                else -> {
                    replaceFrag(createDrop)
                }

            }
            return@setOnNavigationItemSelectedListener true
        }


        fm.beginTransaction().add(R.id.main_frag_container, findDrop, "3").hide(findDrop).commit()
        fm.beginTransaction().add(R.id.main_frag_container, createDrop, "2").hide(createDrop).commit()
        fm.beginTransaction().add(R.id.main_frag_container, mainFragment, "1").commit()

    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    private fun replaceFrag(frag: Fragment) {
        fm.beginTransaction().addToBackStack(frag.tag).hide(active).show(frag)
            .addToBackStack(null).commit()
        active = frag
    }

    @AfterPermissionGranted(LOCATION)
    private fun updateUI() {
        if (auth.currentUser == null) {
            var started = false
            getSharedPreferences("LoginActivity", Context.MODE_PRIVATE).getBoolean("active", started)
            if (!started) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            auth.currentUser!!.reload()
            if (!auth.currentUser!!.isEmailVerified) {
                Log.d(TAG, "updateUI: Email not verified")
                Toast.makeText(this, "Email is not verified, check your emails", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, EmailVerificationActivity::class.java))
            } else {
                locationUtil.updateLastKnownLocation()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        locationUtil.removeLocationUpdates()
    }

}