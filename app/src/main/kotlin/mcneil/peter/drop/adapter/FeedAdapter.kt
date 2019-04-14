package mcneil.peter.drop.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import mcneil.peter.drop.DropApp
import mcneil.peter.drop.DropApp.Companion.appContext
import mcneil.peter.drop.R
import mcneil.peter.drop.model.Drop

interface FeedClickListener {
    fun onItemClicked(v: View, d: Drop)
}

class FeedAdapter(private val dataSet: MutableList<Drop>, private val listener: FeedClickListener) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(), ValueEventListener {
    companion object {
        private const val TAG = "FeedAdapter"
    }

    init {
        DropApp.firebaseUtil.readDrops(this)
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
    }

    override fun getItemCount() = dataSet.size

    override fun onDataChange(ds: DataSnapshot) {
        ds.children.map { d ->
            val drop: Drop? = d.getValue(Drop::class.java)
            if (drop != null) {
                dataSet.add(drop)
            }
        }
        this.notifyDataSetChanged()
    }

    override fun onCancelled(p0: DatabaseError) {}
}