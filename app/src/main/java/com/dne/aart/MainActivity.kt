package com.dne.aart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import androidx.core.view.isVisible
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_sign_up.*

var signedUp: Boolean = false

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment ?: return
        val navController = host.navController


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
        return false
    }
}
