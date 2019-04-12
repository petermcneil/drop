package mcneil.peter.drop.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import kotlinx.android.synthetic.main.activity_settings.*
import mcneil.peter.drop.R

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        a_s_license_button.setOnClickListener(this)
        a_s_login_button.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.a_s_license_button -> openLicense()
            R.id.a_s_login_button -> openLogin()
        }
    }

    private fun openLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun openLicense() {
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
    }
}