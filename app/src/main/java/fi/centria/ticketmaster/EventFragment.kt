package fi.centria.ticketmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.setFragmentResult
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
 * Use the [EventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val rootUrl = "https://app.ticketmaster.com/discovery/v2/"
    private var apikey = "VOHtOfw37AJt3mvrD4VHV80sVG8oecph"
    var eventsArrayList = arrayListOf<EventDataModel>()
    lateinit var eventListview: ListView
    private lateinit var adapter: AllEventAdapter

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
        val view = inflater.inflate(R.layout.fragment_event, container, false)

        // Find the ListView in the layout and set it up
        eventListview = view.findViewById(R.id.eventListview)

        // Launch a coroutine to get event data from the API and populate the ListView
        lifecycleScope.launch {
            val apiString = httpGet((rootUrl+"events.json?apikey="+apikey))
            eventsArrayList = jsonParser(apiString)
            adapter = AllEventAdapter(requireActivity(), eventsArrayList)
            eventListview.adapter = adapter
        }

        // Set up an item click listener for the ListView
        eventListview.setOnItemClickListener { parent, view, position, id ->
            // Get the ID of the clicked event and pass it to the EventDetailsFragment
            val eventId = (eventsArrayList[position].id).toString()
            setFragmentResult("keyfromEventsList", bundleOf("data" to eventId))

            // Replace the bottom part of the layout with the EventDetailsFragment
            val fragmentTransaction = this.parentFragmentManager.beginTransaction()
            val eventDetailsFragment = EventDetailsFragment()
            fragmentTransaction.replace(R.id.bottom_part, eventDetailsFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            // Replace the top part of the layout with the FourthFragment
            val fourthFragment = FourthFragment()
            fragmentTransaction.replace(R.id.top_part, fourthFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)

            // Add the transaction to the back stack and commit it
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }

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

    fun jsonParser(parsee:String): (ArrayList<EventDataModel>) {
        var allEventsData: ArrayList<EventDataModel> = arrayListOf()
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

        val main = JSONTokener(parsee).nextValue() as JSONObject
        val jsonObject = main.getJSONObject("_embedded")
        val jsonArray = jsonObject.getJSONArray("events")
        for (i in 0 until jsonArray.length()) {
            if (jsonArray.getJSONObject(i).has("name")) {
                name = jsonArray.getJSONObject(i).getString("name")
            }
            type = jsonArray.getJSONObject(i).getString("type")
            id = jsonArray.getJSONObject(i).getString("id")
            val jsonArrayimg = jsonArray.getJSONObject(i).getJSONArray("images")
            if (jsonArrayimg.getJSONObject(0).has("url")){
                img = jsonArrayimg.getJSONObject(0).getString("url")
            }

            val jsonObjectDates = jsonArray.getJSONObject(i).getJSONObject("dates")
            val jsonObjectStart = jsonObjectDates.getJSONObject("start")
            date = jsonObjectStart.getString("localDate")
            time = (jsonObjectStart.getString("localTime")).substring(0, 5)

            val jsonObjectEmbd2 = jsonArray.getJSONObject(i).getJSONObject("_embedded")
            val jsonArrayVenues = jsonObjectEmbd2.getJSONArray("venues")
            if (jsonArrayVenues.getJSONObject(0).has("name")) {
                venue = jsonArrayVenues.getJSONObject(0).getString("name")
            }
                val jsonObjectCity = jsonArrayVenues.getJSONObject(0).getJSONObject("city")
                city = jsonObjectCity.getString("name")
                val jsonObjectAddress = jsonArrayVenues.getJSONObject(0).getJSONObject("address")
                address = jsonObjectAddress.getString("line1")

            allEventsData.add(EventDataModel(name,date,time,type,img,city,venue,address,link,id))
        }

        return allEventsData
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}