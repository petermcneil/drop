package mcneil.peter.drop.util

import android.location.Location
import android.util.Log
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.locationUtil
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.User

class FirebaseUtil : GeoFire.CompletionListener {
    private val TAG = this.javaClass.canonicalName
    private val db = FirebaseDatabase.getInstance()
    private val dropDb = db.getReference("drops/")
    private val geoDb = db.getReference("geofire/")
    private val userDb = db.getReference("users/")
    private val geoFire = GeoFire(geoDb)
    private lateinit var lastKnownLocation: Location


    fun writeDrop(dropApp: Drop): String? {
        val nextRef = dropDb.push()

        nextRef.setValue(dropApp)
        return nextRef.key
    }

    fun readDrop(id: String?, listener: ValueEventListener) {
        if (id != null) {
            dropDb.child(id).addListenerForSingleValueEvent(listener)
        } else {
            Log.i(TAG, "Drop id must not be null")
        }
    }

    fun readFeedDrops(listener: ValueEventListener) {
        val id: String? = auth.uid
        if (id != null) {
            Log.d(TAG, String.format("Adding event listener to class: %s", listener.javaClass.simpleName))
            dropDb.orderByChild("ownerId").equalTo(id).addListenerForSingleValueEvent(listener)
            userDb.child(id).addListenerForSingleValueEvent(listener)
        } else {
            Log.e(TAG, String.format("ID not valid: %s", id))
        }
    }

    fun createUser() {
        val id = auth.uid
        if (id != null) {
            Log.i(TAG, String.format("Creating user with id: %s", id))
            val user = User()
            userDb.child(id).setValue(user) { databaseError, databaseReference ->
                if (databaseError != null) {
                    Log.e(TAG, "Data could not be saved: ${databaseError.message}")
                } else {
                    Log.d(TAG, "Data saved successfully.")
                }
            }
        } else {
            Log.e(TAG, "User not logged in")
        }
    }

    fun addFoundDropToUser(listener: ValueEventListener) {
        val id = auth.uid
        if (id != null) {
            userDb.child(id).addListenerForSingleValueEvent(listener)
        } else {
            Log.e(TAG, "User not logged in")
        }
    }


    fun linkDropToLocation(id: String?, location: Location) {
        if (id != null) {
            val geoLocation = GeoLocation(location.latitude, location.longitude)
            geoFire.setLocation(id, geoLocation, this)
        } else {
            Log.e(TAG, "Failed to upload")
        }
    }

    fun dropsForLocation(location: Location, listener: GeoQueryEventListener, radius: Double = 0.01, empty: Boolean = false) {
        if (!::lastKnownLocation.isInitialized) lastKnownLocation = location
        if (locationUtil.fuzzyCheckLocation(location, lastKnownLocation) || empty) {
            val geoLocation = GeoLocation(location.latitude, location.longitude)

            val query = geoFire.queryAtLocation(geoLocation, radius)

            query.addGeoQueryEventListener(listener)
        }
    }


    override fun onComplete(key: String?, error: DatabaseError?) {}
}