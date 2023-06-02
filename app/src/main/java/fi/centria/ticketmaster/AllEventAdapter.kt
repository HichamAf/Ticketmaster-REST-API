package fi.centria.ticketmaster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AllEventAdapter(val context: Context, val dataSource: ArrayList<EventDataModel>) : BaseAdapter() {

    // LayoutInflater is used to inflate a layout XML file into its corresponding View objects
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    // Returns the number of items in the data source
    override fun getCount(): Int {
        return dataSource.size
    }

    // Returns the data object at the specified position in the data source
    override fun getItem(p0: Int): Any {
        return dataSource.get(p0)
    }

    // Returns the ID of the item at the specified position in the data source
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    // Returns a View that displays the data at the specified position in the data source
    override fun getView(rowNumber: Int, row: View?, parent: ViewGroup?): View {

        // Inflate the layout XML file for a single row
        val rowView = inflater.inflate(R.layout.event_row, parent, false)

        // Find the TextViews and ImageView in the inflated row layout
        val eventname = rowView.findViewById<TextView>(R.id.textview_name)
        val eventdate = rowView.findViewById<TextView>(R.id.textview_date)
        val eventtime = rowView.findViewById<TextView>(R.id.textview_time)
        val eventlocal = rowView.findViewById<TextView>(R.id.textview_local)
        val eventimg = rowView.findViewById<ImageView>(R.id.imgview)
        val eventcity = rowView.findViewById<TextView>(R.id.txtview_city)

        // Set the text of the TextViews to display the corresponding data from the data source
        with(dataSource.get(rowNumber)) {
            eventname.text = name
            eventdate.text = date
            eventtime.text = time
            eventlocal.text = type
            eventcity.text = city
            // Glide library to load the image from the URL into the ImageView
            Glide.with(rowView.context)
                .load(img)
                .into(eventimg)
        }
        // Return the row View
        return rowView
    }
}
