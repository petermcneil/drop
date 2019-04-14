package mcneil.peter.drop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mcneil.peter.drop.R
import mcneil.peter.drop.model.Drop

class DropDialogFragment : DialogFragment(), OnMapReadyCallback {
    private val TAG = this.javaClass.simpleName

    companion object {
        fun newInstance(drop: Drop): DropDialogFragment {
            val frag = DropDialogFragment()
            frag.drop = drop
            return frag
        }
    }

    private lateinit var drop: Drop
    private lateinit var message: TextView
    private lateinit var title: TextView
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_drop, container, false)

        message = view.findViewById(R.id.f_d_message)
        title = view.findViewById(R.id.f_d_title)

        message.text = drop.message
        title.text = drop.title
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val mapFragment = fragmentManager?.findFragmentById(R.id.drop_dialog_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN
        val latLng = LatLng(drop.location.latitude, drop.location.longitude)
        map.addMarker(MarkerOptions().position(latLng))
        val center = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        map.moveCamera(center)
    }


}