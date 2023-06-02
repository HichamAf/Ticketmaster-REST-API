package fi.centria.ticketmaster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class EventAdapter(val context: Context, val dataSource: ArrayList<EventDataModel>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return dataSource.size
    }
    override fun getItem(p0: Int): Any {
        return dataSource.get(p0)
    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(rowNumber: Int, row: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.eventdetails_row, parent, false)

        // Find the TextViews and ImageView in the inflated row layout
        val eventname = rowView.findViewById<TextView>(R.id.nametxtview)
        val eventgenre = rowView.findViewById<TextView>(R.id.genretxtview)
        val eventimg = rowView.findViewById<ImageView>(R.id.imgview)
        val eventvenue = rowView.findViewById<TextView>(R.id.venuetxtview)

        // Set the text of the TextViews to display the corresponding data from the data source
        with(dataSource.get(rowNumber)) {
            eventvenue.text = venue
            eventname.text = name
            eventgenre.text = type
            // Glide library to load the image from the URL into the ImageView
            Glide.with(rowView.context)
                .load(img)
                .into(eventimg)
        }
        // Return the row View
        return rowView
    }
}