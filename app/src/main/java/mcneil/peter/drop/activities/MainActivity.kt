package mcneil.peter.drop.activities

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.activity_main.*
import mcneil.peter.drop.R
import mcneil.peter.drop.fragments.LoginFragment

class MainActivity: BaseActivity(), View.OnClickListener {
    private lateinit var fm : FragmentManager
    override fun onClick(v: View) {
        val i = v.id
        when (i) {
            R.id.main_login_button -> openLogin()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fm = supportFragmentManager
        main_login_button.setOnClickListener(this)
    }

    private fun openLogin() {
        val frag = LoginFragment()
        frag.show(fm, "fragment_login")
    }
}