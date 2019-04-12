package mcneil.peter.drop.adapter

import android.util.Log
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

class FeedAdapter(private val dataSet: MutableList<Drop?>) : RecyclerView.Adapter<FeedAdapter.ViewHolder>(), ValueEventListener {

    companion object {
        private const val TAG = "FeedAdapter"
    }

    init {
        DropApp.firebaseUtil.readDrops(this)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView
        val summary: TextView
        val created: TextView
        val location: TextView

        init {
            v.setOnClickListener {
                Log.d(TAG, "Element $adapterPosition clicked.")
            }
            title = v.findViewById(R.id.item_title)
            summary = v.findViewById(R.id.item_summary)
            created = v.findViewById(R.id.item_created)
            location = v.findViewById(R.id.item_location)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view.
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.row_item, viewGroup, false)

        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Log.d(TAG, "Element $position set.")
        val drop = dataSet[position]
        if (drop != null) {
            viewHolder.title.text = drop.title
            viewHolder.summary.text = drop.message
            viewHolder.created.text = appContext.getString(R.string.item_created, drop.formatedDate())
            viewHolder.location.text = drop.location.toString()
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

    override fun onDataChange(ds: DataSnapshot) {
        ds.children.map { d -> dataSet.add(d.getValue(Drop::class.java)) }
        this.notifyDataSetChanged()
    }

    override fun onCancelled(p0: DatabaseError) {}
}