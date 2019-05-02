package mcneil.peter.drop.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.activities.LOCATION
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.DropLocation
import mcneil.peter.drop.model.Either
import mcneil.peter.drop.model.getCurrentDateTime
import mcneil.peter.drop.util.HideKeyboard
import pub.devrel.easypermissions.AfterPermissionGranted


class CreateDropFragment : Fragment(), View.OnClickListener, OnMapReadyCallback {
    private val TAG = this.javaClass.simpleName

    private val input: MutableMap<String, TextInputLayout> = mutableMapOf()
    private lateinit var titleBox: TextInputLayout
    private lateinit var messageBox: TextInputLayout
    private lateinit var map: GoogleMap
    private lateinit var snackBar: Snackbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_create_drop, container, false)

        Log.d(TAG, "Create View")

        titleBox = view.findViewById(R.id.create_drop_title)
        messageBox = view.findViewById(R.id.create_drop_text)

        input["title"] = titleBox
        input["message"] = messageBox

        view.findViewById<AppCompatButton>(R.id.create_drop_button).setOnClickListener(this)
        view.findViewById<View>(R.id.fragment_create_drop).setOnTouchListener(HideKeyboard(activity as Activity))

        snackBar = Snackbar.make(view.findViewById(R.id.fragment_create_drop), "", Snackbar.LENGTH_LONG)
        val mapFragment = childFragmentManager.findFragmentById(R.id.create_drop_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.create_drop_button -> dropMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val f: Fragment? = fragmentManager!!.findFragmentById(R.id.create_drop_map)
        if (f != null) {
            fragmentManager!!.beginTransaction().remove(f).commit()
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION)
    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN

        DropApp.locationUtil.showLocationOnMap(activity as Activity, map)
    }

    private fun dropMessage() {
        if (auth.currentUser != null) {
            val anyFail = input.filter { it.value.editText!!.text.toString().isBlank() }

            if (anyFail.count() > 0) {
                val sb = StringBuilder()

                for (i in 0 until anyFail.count()) {
                    val key = anyFail.keys.elementAt(i)
                    val value = anyFail[key]
                    when (i) {
                        0 -> if (anyFail.count() == 1) {
                            sb.append(key.capitalize())
                        } else {
                            sb.append(key.capitalize())
                        }
                        else -> if (i < anyFail.count() - 1) {
                            sb.append(" $key,")
                        } else {
                            sb.append(" and $key")
                        }
                    }
                    value!!.error = "Cannot be blank."
                }

                sb.append(" must not be blank.")
                snackBar.setText(sb.toString()).show()
            } else {
                input.map { it.value.isErrorEnabled = false }
                val eitherLocation = locationUtil.getLastKnownLocation()
                if (eitherLocation is Either.Right<Location>) {
                    val location = eitherLocation.value
                    val dropLocation = DropLocation(location.latitude, location.longitude)
                    val drop = Drop(title = titleBox.editText!!.text.toString(), message = messageBox.editText!!.text.toString(), location = dropLocation, ownerId = auth.currentUser!!.uid, createdOn = getCurrentDateTime())

                    val id = firebaseUtil.writeDrop(drop)

                    firebaseUtil.linkDropToLocation(id, location)

                    Log.d(tag, "Message: ${drop.message}\n Location: ${location}")

                    snackBar.setText("Saved drop!").show()

                    clearUI()
                } else {
                    Log.e(TAG, "Location not found")
                }
            }
        } else {
            //SHOULD NEVER HAPPEN
            Log.e(tag, "User is not logged in so cannot create a drop")
        }
    }

    private fun clearUI() {
        messageBox.editText!!.setText("")
        titleBox.editText!!.setText("")
    }
}