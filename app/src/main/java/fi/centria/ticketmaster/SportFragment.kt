package fi.centria.ticketmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
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
 * Use the [SportFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SportFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val rootUrl = "https://app.ticketmaster.com/discovery/v2/"
    private var apikey = "VOHtOfw37AJt3mvrD4VHV80sVG8oecph"
    lateinit var eventListview: ListView
    private lateinit var adapter: AttractionAdapter
    lateinit var txtnonfound: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_event, container, false)
        // Find the ListView and TextView UI elements
        eventListview = view.findViewById(R.id.eventListview)
        txtnonfound = view.findViewById(R.id.txtnonfound)

        // Use a coroutine to asynchronously fetch data from the Ticketmaster API
        lifecycleScope.launch {
            val apiString = httpGet(rootUrl+"attractions"+".json?apikey="+apikey)
            var parsedjson = jsonParser(apiString)

            // If there are results from the API, create an adapter for the ListView
            if (parsedjson.isNotEmpty()){
                adapter = AttractionAdapter(requireActivity(), parsedjson)
                eventListview.adapter = adapter

                // When an item in the ListView is clicked, replace the current fragment by ThirdFragment
                eventListview.setOnItemClickListener { parent, view, position, id ->
                    val attractionId = (parsedjson[position].id).toString()
                    setFragmentResult("idfromAttractionsMusicSport", bundleOf("data" to attractionId))
                    val fragmentTransaction = this@SportFragment.parentFragmentManager.beginTransaction()
                    val thirdFragment = ThirdFragment()
                    fragmentTransaction.replace(R.id.bottom_part, thirdFragment)
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            // If there are no results from the API, display a message in the TextView
            else {
                txtnonfound.text = "No music events are planned for the moment"
            }
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
        return result
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
        var type = ""
        var id = ""
        var link = ""
        var img = ""
        var segment = ""
        var genre = ""
        var subgenre = ""
        var upcomming = ""

        val main = JSONTokener(parsee).nextValue() as JSONObject
        val jsonObject = main.getJSONObject("_embedded")
        val jsonArray = jsonObject.getJSONArray("attractions")
        for (i in 0 until jsonArray.length()){
            if (jsonArray.getJSONObject(i).has("name")){
                name = jsonArray.getJSONObject(i).getString("name")
            }
            type = jsonArray.getJSONObject(i).getString("type")
            id = jsonArray.getJSONObject(i).getString("id")
            link = jsonArray.getJSONObject(i).getString("url")
            img = jsonArray.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url")
            segment = jsonArray.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name")
            genre = jsonArray.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name")
            subgenre = jsonArray.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("subGenre").getString("name")
            upcomming = jsonArray.getJSONObject(i).getJSONObject("upcomingEvents").getString("_total")
            if (segment == "Sports"){
                allEventsData.add(EventDataModel(name,"","",segment,img,upcomming,"","","", id))
            }
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
         * @return A new instance of fragment SportFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SportFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}