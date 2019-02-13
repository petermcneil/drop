package mcneil.peter.drop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.R
import mcneil.peter.drop.fragments.LoginFragment


class MainActivity: BaseActivity(), View.OnClickListener {
    private lateinit var fm : FragmentManager
    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.main_login_button -> openLogin()
            R.id.main_license_button -> openLicense()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fm = supportFragmentManager
        main_login_button.setOnClickListener(this)
        main_license_button.setOnClickListener(this)
    }

    private fun openLogin() {
        val frag = LoginFragment()
        frag.show(fm, "fragment_login")
    }

    private fun openLicense() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
    }
}