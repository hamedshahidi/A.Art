package com.dne.aart.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.dne.aart.model.Database
import com.dne.aart.R
import com.dne.aart.util.DataManager
import kotlinx.android.synthetic.main.activity_main.*

const val TAG = "DBG"

var signedUp: Boolean = false

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment ?: return
        val navController = host.navController

        // Fills singleton with data from database
        DataManager.expoList = Database(applicationContext).allExpos

        // navigate to correct destination based on signed in status
        when (checkSignedIn()) {
            true -> findNavController(R.id.nav_host_fragment).navigate(R.id.expoListFragment)
            false -> {
                // Hide bottom nav bar if user is not signed in
                bottom_nav_view.visibility = GONE
                // Navigate to sign up page
                findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
            }
        }
    }

    // Checks if the user is signed in (for navigation purposes)
    private fun checkSignedIn(): Boolean {
        //TODO() check shared preferences for username. Return true if find one.
        return true
    }
}
