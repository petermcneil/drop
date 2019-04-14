package mcneil.peter.drop.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.appContext
import mcneil.peter.drop.DropApp.Companion.auth
import mcneil.peter.drop.DropApp.Companion.firebaseUtil
import mcneil.peter.drop.R
import mcneil.peter.drop.model.Drop
import mcneil.peter.drop.model.User
import org.jetbrains.anko.backgroundColor

interface FeedClickListener {
    fun onItemClicked(v: View, d: Drop)
}

class FeedAdapter(private val dataSet: MutableList<Drop>, private val listener: FeedClickListener) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(), ValueEventListener {
    companion object {
        private const val TAG = "FeedAdapter"
    }

    init {
        DropApp.firebaseUtil.readFeedDrops(this)
    }

    class ViewHolder(v: View, f: FeedClickListener) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.item_title)
        val summary: TextView = v.findViewById(R.id.item_summary)
        val created: TextView = v.findViewById(R.id.item_created)
        val location: TextView = v.findViewById(R.id.item_location)
        var drop: Drop = Drop()

        init {
            v.setOnClickListener {
                f.onItemClicked(v, this.drop)
            }
        }

        fun setBackground(colour: Int) {
            this.itemView.backgroundColor = ContextCompat.getColor(appContext, colour)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item, viewGroup, false)
        return ViewHolder(v, listener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val drop: Drop = dataSet[position]
        viewHolder.drop = drop
        viewHolder.title.text = drop.title
        viewHolder.summary.text = drop.message
        viewHolder.created.text = appContext.getString(R.string.item_created, drop.formatedDate())
        viewHolder.location.text = drop.location.toString()

        if (drop.ownerId == DropApp.auth.uid) {
            viewHolder.setBackground(R.color.drop_feed_mine)
        } else {
            viewHolder.setBackground(R.color.drop_feed_someones)
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onDataChange(ds: DataSnapshot) {
        if (ds.key == auth.uid) {
            Log.d(TAG, "Data key equal to auth id")
            try {
                val user: User? = ds.getValue(User::class.java)
                user?.dropList?.map { dr ->
                    firebaseUtil.readDrop(dr, this)
                } ?: Log.e(TAG, "Unknown data")
            } catch (e: DatabaseException) {
                Log.e(TAG, "User cannot be created from data: ${e.message}")
            }
        } else {
            if(ds.key == "drops") {
                Log.d(TAG, "List of drops to convert")
                ds.children.map { d ->
                    try {
                        val drop: Drop? = d.getValue(Drop::class.java)
                        if (drop != null) {
                            Log.d(TAG, "Drop found: $drop")
                            dataSet.add(drop)
                        } else {
                        }
                    } catch (e: DatabaseException) {
                        Log.e(TAG, "Drop cannot be created from data")
                    }
                }
            } else {
                Log.d(TAG, "Single drop")
                try {
                    val drop: Drop? = ds.getValue(Drop::class.java)
                    if (drop != null) {
                        Log.d(TAG, "Drop found: $drop")
                        dataSet.add(drop)
                    } else {
                    }
                } catch (e: DatabaseException) {
                    Log.e(TAG, "Drop cannot be created from data")
                }
            }

        }
        this.notifyDataSetChanged()
    }

    override fun onCancelled(p0: DatabaseError) {}
}