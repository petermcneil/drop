package mcneil.peter.drop.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mcneil.peter.drop.R
import mcneil.peter.drop.adapter.FeedAdapter
import mcneil.peter.drop.model.Drop

class MainFragment : Fragment(), View.OnClickListener {
    private val TAG = "MainFragment"

    private lateinit var fm: FragmentManager
    private lateinit var myContext: FragmentActivity
    private lateinit var recycleView: RecyclerView
    private lateinit var dataset: MutableList<Drop?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataset = mutableListOf()
    }

    //    private fun initDataset() {
    //
    //        val drop = Drop(message = "A nice message", location = DropLocation(0.0, 0.0), ownerId = "")
    //    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        recycleView = view.findViewById(R.id.main_view)

        recycleView.adapter = FeedAdapter(dataset)
        recycleView.layoutManager = LinearLayoutManager(activity)

//        view.findViewById<AppCompatButton>(R.id.main_btn_create).setOnClickListener(this)
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
//            R.id.main_btn_create -> {
//                Log.i(TAG, "create button")
//                fm.beginTransaction().replace(R.id.activity_main, CreateDropFragment()).addToBackStack("Create Drop")
//                    .commit()
//            }
        }
    }
}