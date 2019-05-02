package mcneil.peter.drop.util

import android.location.Location
import android.util.Log
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.*
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.listeners.CheckUserDropListener
import mcneil.peter.drop.listeners.LocationDropListener
import mcneil.peter.drop.model.ACallback
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
    private lateinit var geoQuery: GeoQuery

    init {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            userDb.child(userId).keepSynced(true)
        }
    }

    fun writeDrop(dropApp: Drop): String? {
        val nextRef = dropDb.push()

        nextRef.setValue(dropApp)
        return nextRef.key
    }

    fun writeUser(user: User, id: String) {
        userDb.child(id).setValue(user)
    }

    fun readDrop(id: String?, listener: ValueEventListener) {
        if (id != null) {
            dropDb.child(id).addListenerForSingleValueEvent(listener)
        } else {
            Log.d(TAG, "Drop id must not be null")
        }
    }

    fun readFeedDrops(dropListener: ChildEventListener, userListener: ChildEventListener) {
        val id: String? = auth.uid
        if (id != null) {
            Log.d(TAG, "Adding child listener to class: ${dropListener.javaClass.simpleName}")
            dropDb.orderByChild("ownerId").equalTo(id).addChildEventListener(dropListener)
            Log.d(TAG, "Adding child listener to class: ${userListener.javaClass.simpleName}")
            userDb.child(id).child("dropList").addChildEventListener(userListener)
        } else {
            Log.e(TAG, "ID not valid: $id")
        }
    }

    fun createUser() {
        val id = auth.uid
        if (id != null) {
            val user = User(name = auth.currentUser?.displayName)
            Log.d(TAG, "Creating user: $user with id: $id")
            userDb.child(id).setValue(user) { databaseError, _ ->
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

    fun getUser(userCallback: ACallback<User>) {
        val id = auth.uid
        if (id != null) {
            userDb.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(ds: DataSnapshot) {
                    val user = ds.getValue(User::class.java)
                    if (user != null) {
                        userCallback.callback(user)
                    }
                }

            })
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


    /**
     * Starts a chain of ValueEventListeners to find a drop that
     * the user has not found yet.
     */
    fun findNewDrop(location: Location, callback: ACallback<Pair<String, Drop>>, radius: Double) {
        dropsForLocation(location, LocationDropListener(callback), radius)
    }

    private fun dropsForLocation(location: Location, listener: GeoQueryEventListener, radius: Double = 0.01) {
        Log.d(TAG, "dropsForLocation: Called")
        if (!::lastKnownLocation.isInitialized) lastKnownLocation = location

        val geoLocation = GeoLocation(location.latitude, location.longitude)

        Log.d(TAG, "dropsForLocation: Building GeoQuery")
        geoQuery = geoFire.queryAtLocation(geoLocation, radius)

        Log.d(TAG, "dropsForLocation: Add listener")
        geoQuery.addGeoQueryEventListener(listener)
    }

    /**
     * Function to check if user has already collected the drop
     */
    fun checkUserDrop(dropId: String, callback: ACallback<Pair<String, Drop>>) {
        val authId = auth.currentUser?.uid
        Log.d(TAG, "checkUserDrop: called")

        if (authId != null) {
            userDb.child(authId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(de: DatabaseError) {}

                override fun onDataChange(ds: DataSnapshot) {
                    val user = ds.getValue(User::class.java)
                    if (user != null) {
                        Log.d(TAG, "checkUserDrop: User found")
                        val listener = CheckUserDropListener(authId, geoQuery, callback)
                        val dropList = user.dropList
                        if (dropList != null) {
                            Log.d(TAG, "checkUserDrop: Drop list isn't null")
                            if (!dropList.contains(dropId)) {
                                Log.d(TAG, "checkUserDrop: Drop isn't in user list")
                                dropDb.child(dropId).addListenerForSingleValueEvent(listener)
                            }
                        } else {
                            Log.d(TAG, "checkUserDrop: Drop list is empty, finding drops")
                            dropDb.child(dropId).addListenerForSingleValueEvent(listener)
                        }
                    }

                }
            })
        }
    }

    override fun onComplete(key: String?, error: DatabaseError?) {}

}