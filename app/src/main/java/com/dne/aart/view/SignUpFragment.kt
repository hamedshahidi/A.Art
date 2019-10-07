package com.dne.aart.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.dne.aart.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_art_list.view.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.android.synthetic.main.fragment_sign_up.view.*

var sharedPrefFile: String = "kotlinsharedpreference"

class SignUpFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private var deviceID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // check if fragment is added initialize sharedpreferences
        if (isAdded) {
            sharedPreferences = activity!!.getSharedPreferences(
                sharedPrefFile,
                Context.MODE_PRIVATE
            )

            // Get the phone UUID
            deviceID = Settings.Secure.getString(
                context!!.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            Log.d("UUID", deviceID)
        }

        // At sign-up put the username and UUID to the shared preferences
        signupButton.setOnClickListener {
            val username = usernameTextView.text.toString()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.putString("uuid", deviceID)
            editor.apply()

            // Test Test Test
            Log.d("USERNAME", sharedPreferences.getString("username", "No Value"))
            Log.d("UUID", sharedPreferences.getString("uuid", "No Value"))
            Log.d("UUID", sharedPreferences.all.toString())
            //Toast.makeText(this, sharedNameValue, Toast.LENGTH_SHORT).show()

            navigateToExpoList()
        }
    }

    private fun navigateToExpoList() {
        findNavController().navigate(R.id.expoListFragment, null, navOptions.options)
    }
}
