package mcneil.peter.drop.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import mcneil.peter.drop.R

class AccountFragment : Fragment(), View.OnClickListener {
    private lateinit var myContext: FragmentActivity
    private lateinit var fm: FragmentManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        view.findViewById<AppCompatButton>(R.id.main_login_button).setOnClickListener(this)
        view.findViewById<AppCompatButton>(R.id.main_license_button).setOnClickListener(this)

        fm = myContext.supportFragmentManager

        return view
    }

    override fun onAttach(context: Context?) {
        myContext = activity as FragmentActivity
        super.onAttach(context)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_license_button -> openLicense()
            R.id.main_login_button -> fm.beginTransaction().replace(R.id.activity_main, LoginFragment()).commit()
        }
    }


    private fun openLicense() {
        startActivity(Intent(context, OssLicensesMenuActivity::class.java))
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.custom_license_title))
    }

}