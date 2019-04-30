package mcneil.peter.drop.listeners

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.model.User

/**
 * Listener used to add a drop to a user
 */
class AddDropToUserListener(private val dropId: String) : ValueEventListener {
    private val TAG = this.javaClass.simpleName

    override fun onDataChange(ds: DataSnapshot) {
        val user = ds.getValue(User::class.java)

        if (user != null) {
            val list = user.dropList
            if (list != null) {
                Log.d(TAG, "List already exists")
                if(!list.contains(dropId)) {
                    val updatedList = list.toMutableList().apply { add(dropId) }

                    updateUser(user, updatedList, ds.key)
                } else {
                    Log.d(TAG, "Drop id already in user: $dropId")
                }
            } else {
                Log.d(TAG, "List is null, creating a new one")
                val newList = List(1) { dropId }

                updateUser(user, newList, ds.key)
            }
        } else {
            Log.e(TAG, "User is null for some reason")
        }
    }

    override fun onCancelled(p0: DatabaseError) {}

    private fun updateUser(oldUser: User, updatedList: List<String>, id: String?) {
        val updatedUser = User(name = oldUser.name, dropList = updatedList)

        Log.d(TAG, "New user: $updatedUser")
        if (id != null) {
            DropApp.firebaseUtil.writeUser(updatedUser, id)
        }
    }
}