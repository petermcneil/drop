package mcneil.peter.drop.util

import android.location.Location
import android.util.Log
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.model.Drop

class FirebaseUtil : GeoFire.CompletionListener {
    private val TAG = this.javaClass.canonicalName

    private val db = FirebaseDatabase.getInstance()
    private val dropDb = db.getReference("drops/")
    private val geoDb = db.getReference("geofire/")
    private val geoFire = GeoFire(geoDb)

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

    fun linkDropToLocation(id: String?, location: Location) {
        if (id != null) {
            val geoLocation = GeoLocation(location.latitude, location.longitude)

            geoFire.setLocation(id, geoLocation, this)
        } else {
            Log.e(TAG, "Failed to upload")
        }
    }

    fun dropsForLocation(location: Location, listener: GeoQueryEventListener, radius: Double = 0.01) {
        val geoLocation = GeoLocation(location.latitude, location.longitude)

        val query = geoFire.queryAtLocation(geoLocation, radius)

        query.addGeoQueryEventListener(listener)
    }


    override fun onComplete(key: String?, error: DatabaseError?) {}
}