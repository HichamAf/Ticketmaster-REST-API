package fi.centria.ticketmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
    // This function is called when the activity is created. It sets the content view
    // to the activity_main.xml layout file, which contains a top and bottom fragment container.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // The action bar is hidden with the `hide()` function call.
        supportActionBar?.hide()

        // Instantiate the top and bottom fragments and add them to the corresponding
        // fragment containers using a FragmentManager and a FragmentTransaction.
        var topFragment = FirstFragment()
        var bottomFragment = SecondFragment()
        supportFragmentManager.commit {
            // this function allow fragment reordering
            setReorderingAllowed(true)
            // add the fragments to their containers.
            add(R.id.top_part, topFragment)
            add(R.id.bottom_part, bottomFragment)
        }
    }
}