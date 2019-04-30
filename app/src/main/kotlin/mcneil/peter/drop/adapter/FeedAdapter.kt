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
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.appContext
import mcneil.peter.drop.R
import mcneil.peter.drop.listeners.FeedDropListener
import mcneil.peter.drop.listeners.FeedUserDropListener
import mcneil.peter.drop.model.Drop
import org.jetbrains.anko.backgroundColor
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

interface FeedClickListener {
    fun onItemClicked(v: View, d: Drop)
}

class FeedAdapter(private val dataSet: MutableMap<String, Drop>, private val listener: FeedClickListener) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(), ValueEventListener {
    private val TAG = this.javaClass.simpleName
    private val dropPosition = mutableMapOf<String, Int>()

    private var firstRun = 0

    init {
        val feedDrop = FeedDropListener(this)
        DropApp.firebaseUtil.readFeedDrops(feedDrop, FeedUserDropListener(this))
        firstRun = dataSet.size - 1
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
        val properPosition = (dataSet.size - 1) - position
        val id = dataSet.keys.toList()[properPosition]

        //The dropPosition should only be set on the first run
        if (firstRun > 0) {
            dropPosition[id] = properPosition
            firstRun--
        }

        //Add drop to recycler view
        val drop: Drop? = dataSet[id]
        if (drop != null) {
            viewHolder.drop = drop
            viewHolder.title.text = drop.title
            viewHolder.summary.text = drop.message
            viewHolder.created.text = appContext.getString(R.string.item_created, drop.formattedDate())
            viewHolder.location.text = drop.location.toString()

            if (drop.ownerId == DropApp.auth.uid) {
                viewHolder.setBackground(R.color.drop_feed_mine)
            } else {
                viewHolder.setBackground(R.color.drop_feed_someones)
            }
        }
    }

    override fun onDataChange(ds: DataSnapshot) {
        val drop = ds.getValue(Drop::class.java)
        if (drop != null) {
            val id = ds.key
            if (id != null) {
                this.updateDataset(mapOf(id to drop))
            }
        }
    }

    fun updateDataset(dropMap: Map<String, Drop>) {
        Log.d(TAG, "Updating dataset and view")
        //Creates a copy of the dataset for modification
        val tempDataSet = dataSet.toMutableMap()
        for ((key, value) in dropMap) {
            tempDataSet[key] = value
        }

        //Sort tempDataSet by date
        val sorted = tempDataSet.toList().toSortedSet(Comparator { o1, o2 ->
            val d1 = SimpleDateFormat(Drop.date_pattern, Locale.getDefault()).parse(o1.second.createdOn)
            val d2 = SimpleDateFormat(Drop.date_pattern, Locale.getDefault()).parse(o2.second.createdOn)

            when {
                d1.before(d2) -> -1
                d1.after(d2) -> 1
                else -> 0
            }
        })

        //Clear feed
        this.dataSet.clear()
        this.notifyDataSetChanged()

        //Generate positions for drops
        this.dropPosition.clear()

        var i = tempDataSet.size - 1
        for ((key, _) in sorted) {
            dropPosition[key] = i
            i--
        }

        //Draw all in dataset
        this.dataSet.putAll(sorted)
        this.notifyDataSetChanged()
    }

    fun removeDrop(id: String) {
        val position = dropPosition[id]
        if (position != null) {
            Log.d(TAG, "Removing position: $position : $id")
            dataSet.remove(id)
            this.notifyItemRemoved(position)
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onCancelled(de: DatabaseError) {}

}