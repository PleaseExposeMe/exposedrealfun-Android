package de.biSlaveNumberOne.exposedrealfun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapterBookmarks(private val dataSet: MutableList<String>) :
    RecyclerView.Adapter<CustomAdapterBookmarks.ViewHolder>() {

    private lateinit var mListnerClick: onItemClickListner
    private lateinit var mListnerLong: onLongClickListner

    interface onItemClickListner{

        fun onItemClick(position: Int)

    }

    interface onLongClickListner{

            fun onLongClick(pos: Int) {

            }


    }

    fun setOnItemClickListener(listener: onItemClickListner){

        mListnerClick = listener

    }

    fun setOnLongClickListener(listener: onLongClickListner) {
        mListnerLong = listener
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: onItemClickListner, listenerLong: onLongClickListner) : RecyclerView.ViewHolder(view) {
        val textView: TextView


        init {
            // Define click listener for the ViewHolder's View.
            textView = view.findViewById(R.id.textView)
            view.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
            view.setOnLongClickListener {
                listenerLong.onLongClick(adapterPosition)
                true
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view, mListnerClick, mListnerLong)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.textView.text = dataSet[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}