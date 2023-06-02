package fi.centria.ticketmaster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentTransaction

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var all_event_btn: Button
    lateinit var music_event_btn: Button
    lateinit var sport_event_btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // inflates the layout from the fragment_first.xml file and returns the root view for the fragment
        var view = inflater.inflate(R.layout.fragment_first, container, false)
        all_event_btn = view.findViewById(R.id.all_events_btn)
        music_event_btn = view.findViewById(R.id.music_event_btn)
        sport_event_btn = view.findViewById(R.id.sports_event_btn)

        //display all events when this fragment starts
        // creates a new EventFragment and replaces the current fragment in the bottom part of the screen with it
        var fragmentTransaction = this.parentFragmentManager.beginTransaction()
        var eventFragment = EventFragment()
        fragmentTransaction.replace(R.id.bottom_part, eventFragment)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction.addToBackStack(null) // adds the current fragment to the back stack
        fragmentTransaction.commit() // commits the changes to the fragment transaction

        // sets click listeners for the buttons
        all_event_btn.setOnClickListener({
            var fragmentTransaction = this.parentFragmentManager.beginTransaction()
            var eventFragment = EventFragment()
            fragmentTransaction.replace(R.id.bottom_part, eventFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        music_event_btn.setOnClickListener({
            var fragmentTransaction = this.parentFragmentManager.beginTransaction()
            var musicFragment = MusicFragment()
            fragmentTransaction.replace(R.id.bottom_part, musicFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        sport_event_btn.setOnClickListener({
            var fragmentTransaction = this.parentFragmentManager.beginTransaction()
            var sportFragment = SportFragment()
            fragmentTransaction.replace(R.id.bottom_part, sportFragment)
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        })
        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FirstFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}