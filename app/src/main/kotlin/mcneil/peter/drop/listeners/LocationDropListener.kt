package mcneil.peter.drop.listeners

import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.firebase.database.DatabaseError
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.model.ACallback
import mcneil.peter.drop.model.Drop

class LocationDropListener(private val callback: ACallback<Pair<String, Drop>>) : GeoQueryEventListener {

    override fun onGeoQueryReady() {}

    override fun onKeyEntered(key: String, location: GeoLocation) {
        firebaseUtil.checkUserDrop(key, callback)
    }

    override fun onKeyMoved(key: String?, location: GeoLocation?) {}

    override fun onKeyExited(key: String?) {}

    override fun onGeoQueryError(error: DatabaseError?) {}

}