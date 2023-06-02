package fi.centria.ticketmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
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
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: EventAdapter
    private val rootUrl = "https://app.ticketmaster.com/discovery/v2/"
    private var apikey = "VOHtOfw37AJt3mvrD4VHV80sVG8oecph"
    lateinit var name: TextView
    lateinit var segment: TextView
    lateinit var genre: TextView
    lateinit var link: TextView
    lateinit var imgview: ImageView
    lateinit var upcoming: TextView

    var allEventsData: ArrayList<EventDataModel> = arrayListOf()
    var attractionId = ""

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
        var view = inflater.inflate(R.layout.fragment_third, container, false)
        name = view.findViewById(R.id.name_txtview)
        segment = view.findViewById(R.id.segment_txtview)
        genre = view.findViewById(R.id.genre_txtview)
        link = view.findViewById(R.id.link_txtview)
        upcoming = view.findViewById(R.id.upcoming_txtview)
        imgview = view.findViewById(R.id.imgview)

        setFragmentResultListener("idfromAttractionsMusicSport"){key, bundle ->
            var r = bundle.getString("data")
            if (r != null) {
                attractionId = r
            }
            var apiurl: String = ""
            lifecycleScope.launch {
                //"https://app.ticketmaster.com/discovery/v2/attractions/K8vZ9175BhV.json?apikey=VOHtOfw37AJt3mvrD4VHV80sVG8oecph"
                apiurl = httpGet(rootUrl+"attractions/"+attractionId+".json?apikey="+apikey)
                var parsedjson= arrayListOf<EventDataModel>()
                parsedjson = jsonParser(apiurl)
                if (parsedjson.isNotEmpty()) {
                    name.text = parsedjson[0].name
                    segment.text = parsedjson[0].date // I stored segment value in date key
                    genre.text = parsedjson[0].time //samething here
                    upcoming.text = parsedjson[0].type
                    link.text = parsedjson[0].link
                    //imgview = view.findViewById(R.id.imgview)

                    val img = parsedjson[0].img
                    Glide.with(view.context)
                        .load(img)
                        .into(imgview)
                }
                else {
                    "No event found for the moment"
                }
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
        var type = ""
        var id = ""
        var link = ""
        var youtube = ""
        var twitter = ""
        var facebook = ""
        var wiki = ""
        var instagram = ""
        var img = ""
        var segment = ""
        var genre = ""
        var upcomming = ""
        var address = ""

        val main = JSONTokener(parsee).nextValue() as JSONObject
        name = main.getString("name")
        link = main.getString("url")
        val jsonArrayImg = main.getJSONArray("images")
        img = jsonArrayImg.getJSONObject(0).getString("url")
        segment = main.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name")
        genre = main.getJSONArray("classifications").getJSONObject(0).getJSONObject("genre").getString("name")
        upcomming = main.getJSONObject("upcomingEvents").getString("_total")
        allEventsData.add(EventDataModel(name,segment,genre,upcomming,img,"","","",link, ""))
        return allEventsData
    }
    fun jsonParser2(parsee:String): (ArrayList<EventDataModel>) {
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

        val main = JSONTokener(parsee).nextValue() as JSONObject
        val jsonObject = main.getJSONObject("_embedded")
        val jsonArray = jsonObject.getJSONArray("events")
        for (i in 0 until jsonArray.length()){
            val jsonObjectDates = jsonArray.getJSONObject(i).getJSONObject("dates")
            val jsonObjectStart = jsonObjectDates.getJSONObject("start")
            date = jsonObjectStart.getString("localDate")
            time = (jsonObjectStart.getString("localTime")).substring(0,5)
            val jsonObjectEmbd2 = jsonArray.getJSONObject(i).getJSONObject("_embedded")
            val jsonArrayVenues = jsonObjectEmbd2.getJSONArray("venues")
            for (j in 0 until jsonArrayVenues.length()){
                if (jsonArrayVenues.getJSONObject(j).has("name")) {
                    venue = jsonArrayVenues.getJSONObject(j).getString("name")
                    val jsonObjectCity = jsonArrayVenues.getJSONObject(j).getJSONObject("city")
                    city = jsonObjectCity.getString("name")
                    val jsonObjectAddress = jsonArrayVenues.getJSONObject(j).getJSONObject("address")
                    address = jsonObjectAddress.getString("line1")
                }
            }
            val jsonArrayAttraction = jsonObjectEmbd2.getJSONArray("attractions")
            for (k in 0 until jsonArrayAttraction.length()){
                if (jsonArrayAttraction.getJSONObject(k).has("name")) {
                    name = jsonArrayAttraction.getJSONObject(k).getString("name")
                }
                link = jsonArrayAttraction.getJSONObject(k).getString("url")
                val jsonArrayimg = jsonArrayAttraction.getJSONObject(k).getJSONArray("images")
                for (l in 0 until jsonArrayimg.length()){
                    if (jsonArrayimg.getJSONObject(l).has("url")){
                        img = jsonArrayimg.getJSONObject(l).getString("url")
                    }
                }
                val jsonArrayClassifi = jsonArrayAttraction.getJSONObject(k).getJSONArray("classifications")
                for (k in 0 until jsonArrayClassifi.length()){
                    if (jsonArrayClassifi.getJSONObject(k).has("segment")){
                        val jsonObjectSegm = jsonArrayClassifi.getJSONObject(k).getJSONObject("segment")
                        type = jsonObjectSegm.getString("name")
                    }
                }
            }
            if (type == "Music"){
                allEventsData.add(EventDataModel(name,date,time,type,img,city,venue,address,link,""))
            }
        }
        when (return allEventsData) {
            allEventsData.isNotEmpty() -> {}
            else -> {"No event found"}
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThirdFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}