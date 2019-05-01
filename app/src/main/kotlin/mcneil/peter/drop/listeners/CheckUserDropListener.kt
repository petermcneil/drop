package mcneil.peter.drop.listeners

import android.util.Log
import com.firebase.geofire.GeoQuery
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.model.ACallback
import mcneil.peter.drop.model.Drop

class CheckUserDropListener(private val authId: String, private val geoQuery: GeoQuery, private val callback: ACallback<Pair<String, Drop>>) : ValueEventListener {
    private val TAG = this.javaClass.simpleName
    override fun onCancelled(de: DatabaseError) {}

    override fun onDataChange(ds: DataSnapshot) {
        val key = ds.key
        val drop = ds.getValue(Drop::class.java)
        if (drop != null && key != null) {
            if (authId != drop.ownerId) {
                Log.d(TAG, "checkUserDrop: Drop is a new one, removing all listeners")
                geoQuery.removeAllListeners()
                callback.callback(Pair(key, drop))
            } else {
                Log.d(TAG, "checkUserDrop: Drop is owned by logged in user")
            }
        }
    }

}