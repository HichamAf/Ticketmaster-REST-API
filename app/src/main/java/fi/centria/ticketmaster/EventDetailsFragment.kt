package fi.centria.ticketmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EventDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val rootUrl = "https://app.ticketmaster.com/discovery/v2/"
    private var apikey = "VOHtOfw37AJt3mvrD4VHV80sVG8oecph"
    private lateinit var adapter: EventAdapter
    var eventsArrayList = arrayListOf<EventDataModel>()
    lateinit var eventListview: ListView
    lateinit var nametxt: TextView
    lateinit var datetxt: TextView
    lateinit var timetxt: TextView
    lateinit var localtxt: TextView
    lateinit var citytxt: TextView
    lateinit var infotxt: TextView
    lateinit var imgtxt: ImageView
    lateinit var address: TextView
    lateinit var venue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_event_details, container, false)
        // Find the ListView in the inflated layout
        eventListview = view.findViewById(R.id.eventListview)
        var eventId=""
        // Set up a listener for a fragment result with key "keyfromEventsList"
        setFragmentResultListener("keyfromEventsList") { key, bundle ->
            // Get the data string from the result bundle
            var r = bundle.getString("data")
            if (r != null) {
                eventId = r
            }
            // Using eventId to make an HTTP request for event details
            // and update the ListView with the results
            lifecycleScope.launch {
                var eventdetails = httpGet(rootUrl+"events/"+eventId+".json?apikey="+apikey)
                eventsArrayList = jsonParser(eventdetails)
                adapter = EventAdapter(requireActivity(), eventsArrayList)
                eventListview.adapter = adapter
            }
        }
        // Set up a listener for clicks on items in the ListView
        eventListview.setOnItemClickListener { parent, view, position, id ->
            // Get the eventId for the clicked item and send it as a result to another fragment
            val eventId = (eventsArrayList[position].id).toString()
            setFragmentResult("idfromAttractionsMusicSport", bundleOf("data" to eventId))
            // Replace the current fragment with a new one and add it to the back stack
            val fragmentTransaction = this.parentFragmentManager.beginTransaction()
            val thirdFragment = ThirdFragment()
            fragmentTransaction.replace(R.id.bottom_part, thirdFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        // Return the inflated view
        return view
    }


    suspend fun httpGet(myUrl: String?): String {
        val result = withContext(Dispatchers.IO) {
            val inputStream: InputStream
            val url: URL = URL(myUrl)
            val conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
            conn.connect()
            inputStream = conn.inputStream
            if (inputStream != null) {
                convertInputStreamToString(inputStream)
            } else {
                "Not working!"
            }
        }
        return result.toString()
    }

    fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))
        var line: String? = bufferedReader?.readLine()
        var result: String = ""
        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }
        inputStream.close()
        return result
    }

    fun jsonParser(parsee: String): ArrayList<EventDataModel> {
        // Initialize an empty ArrayList to hold EventDataModel objects
        var allEventsData: ArrayList<EventDataModel> = arrayListOf()
        // Initialize variables to hold data for each event
        var name = ""
        var date = ""
        var time = ""
        var type = ""
        var img = ""
        var city = ""
        var venue = ""
        var address = ""
        var link = ""
        var id = ""
        var segment = ""
        var genre = ""

        // Parse the input JSON string using the JSONTokener and JSONObject classes
        val main = JSONTokener(parsee).nextValue() as JSONObject
        // Get the "_embedded" object from the JSON and extract the "attractions" array
        val jsonObject = main.getJSONObject("_embedded")
        val jsonArray = jsonObject.getJSONArray("attractions")
        // Loop through each object in the "attractions" array
        for (i in 0 until jsonArray.length()) {
            // If the object has a "name" property, extract its value
            if (jsonArray.getJSONObject(i).has("name")) {
                name = jsonArray.getJSONObject(i).getString("name")
            }
            // Extract the ID and image URL for the event
            id = jsonArray.getJSONObject(i).getString("id")
            val jsonArrayimg = jsonArray.getJSONObject(i).getJSONArray("images")
            img = jsonArrayimg.getJSONObject(0).getString("url")
            // Extract the segment and genre names from the classifications array
            val jsonArrayClassif = jsonArray.getJSONObject(i).getJSONArray("classifications")
            segment = jsonArrayClassif.getJSONObject(0).getJSONObject("segment").getString("name")
            genre = jsonArrayClassif.getJSONObject(0).getJSONObject("genre").getString("name")

            // Create a new EventDataModel object with the extracted data and add it to the ArrayList
            allEventsData.add(EventDataModel(name,date,time,segment,img,city,genre,address,link,id))
        }
        // Return the ArrayList of EventDataModel objects
        return allEventsData
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}