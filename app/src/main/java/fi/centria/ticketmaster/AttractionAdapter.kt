package fi.centria.ticketmaster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class AttractionAdapter(val context: Context, val dataSource: ArrayList<EventDataModel>) : BaseAdapter() {
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
        val rowView = inflater.inflate(R.layout.attraction_row, parent, false)

        val nameattr = rowView.findViewById<TextView>(R.id.textview_name)
        val segment = rowView.findViewById<TextView>(R.id.segment_txtview)
        val genre = rowView.findViewById<TextView>(R.id.genre_txtview)
        val upcomingevent = rowView.findViewById<TextView>(R.id.upcoming_txtview)
        val attrimg = rowView.findViewById<ImageView>(R.id.imgview)

        // Set the text of the TextViews to display the corresponding data from the data source
        with(dataSource.get(rowNumber)) {
            nameattr.text = name
            segment.text = type
            genre.text = venue
            upcomingevent.text = city

            // Glide library to load the image from the URL into the ImageView
            Glide.with(rowView.context)
                .load(img)
                .into(attrimg)
        }
        // Return the row View
        return rowView
    }
}