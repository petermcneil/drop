package mcneil.peter.drop.listeners

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.adapter.FeedAdapter
import mcneil.peter.drop.model.Drop

/**
 * Listens to the drops that are owned by the user.
 *
 * When one is created, changed, or removed the feed adapter
 * is updated.
 */
class FeedDropListener(private val feedAdapter: FeedAdapter) : ChildEventListener {
    private val TAG = this.javaClass.simpleName

    override fun onChildChanged(ds: DataSnapshot, p1: String?) {
        onDataChange(ds)
    }

    override fun onChildAdded(ds: DataSnapshot, p1: String?) {
        onDataChange(ds)
    }

    override fun onChildRemoved(ds: DataSnapshot) {
        val drop = ds.getValue(Drop::class.java)
        if (drop != null) {
            val id = ds.key
            if (id != null) {
                feedAdapter.removeDrop(id)
            }
        }
    }

    override fun onCancelled(de: DatabaseError) {}
    override fun onChildMoved(ds: DataSnapshot, p1: String?) {}

    private fun onDataChange(ds: DataSnapshot) {
        val dataSet = mutableMapOf<String, Drop>()
        if (ds.key == "drops") {
            Log.d(TAG, "List of drops to convert")
            ds.children.map { d ->
                val drop: Drop? = d.getValue(Drop::class.java)
                if (drop != null) {
                    Log.d(TAG, "Drop found: $drop")
                    val key = d.key
                    if (key != null) {
                        dataSet[key] = drop
                    }
                }
            }
        } else {
            Log.d(TAG, "Single drop")
            val drop: Drop? = ds.getValue(Drop::class.java)
            if (drop != null) {
                Log.d(TAG, "Drop found: $drop")
                val key = ds.key
                if (key != null) {
                    dataSet[key] = drop
                }
            }
        }
        feedAdapter.updateDataset(dataSet)
    }
}

/**
 * ChildEventListener that listens to the Drop List of a User.
 *
 * When the drop list is changed, the FeedAdapter gets updated.
 */
class FeedUserDropListener(private val feedAdapter: FeedAdapter) : ChildEventListener {
    private val TAG = this.javaClass.simpleName

    override fun onChildChanged(ds: DataSnapshot, p1: String?) {
        onDataChange(ds)
    }

    override fun onChildAdded(ds: DataSnapshot, p1: String?) {
        onDataChange(ds)
    }

    override fun onChildRemoved(ds: DataSnapshot) {
        val dropId = ds.getValue(String::class.java)
        if (dropId != null) {
            Log.d(TAG, "Drop ($dropId) removed from user")
            feedAdapter.removeDrop(dropId)
        }
    }

    override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

    private fun onDataChange(ds: DataSnapshot) {
        Log.d(TAG, "Data changed for user: $ds")
        val key = ds.key
        if (key != null) {
            try {
                Integer.parseInt(key)
                readId(ds)
            } catch (e: NumberFormatException) {
                ds.children.forEach { d ->
                    readId(d)
                }
            }
        }
    }

    private fun readId(ds: DataSnapshot) {
        val id = ds.getValue(String::class.java)
        if (id != null) {
            DropApp.firebaseUtil.readDrop(id, feedAdapter)
        }
    }

    override fun onCancelled(p0: DatabaseError) {}
}