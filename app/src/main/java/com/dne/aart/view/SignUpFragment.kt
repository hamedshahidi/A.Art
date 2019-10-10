package com.dne.aart.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.dne.aart.R
import kotlinx.android.synthetic.main.fragment_sign_up.*

var sharedPrefFile: String = "kotlinsharedpreference"
//val admin = false


class SignUpFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bundle: Bundle
    private var deviceID: String? = null
    private var username: String = ""
    private var isAdmin: Boolean= false


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

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isAdmin = checkIfAdmin()

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
            Log.d("UUID", "uuid: $deviceID")
        }

        // At sign-up put the username and UUID to the shared preferences
        signupButton.setOnClickListener {
            username = usernameTextView.text.toString()
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.putString("username", username)
            editor.putString("uuid", deviceID)
            editor.apply()

            // Test Test Test
            Log.d("USERNAME", sharedPreferences.getString("username", "No Value"))
            Log.d("UUID", sharedPreferences.getString("uuid", "No Value"))
            Log.d("UUID", sharedPreferences.all.toString())

            isAdmin = checkIfAdmin()
            Log.d("DBGADMIN", isAdmin.toString())

            // if user is admin start cloud anchor hosting navigation route
            bundle = bundleOf("isAdmin" to isAdmin)

            if (username.isEmpty() || username.isBlank()) {
                Toast.makeText(this.context, "Please enter username", Toast.LENGTH_LONG).show()
            } else {
                usernameTextView.hideKeyboard()
                if (isAdmin) navigateToArtList()
                else navigateToExpoList()

            }


        }
    }

    private fun navigateToArtList() {
        findNavController().navigate(R.id.artListFragment, bundle, navOptions.options)
    }

    private fun navigateToExpoList() {
        findNavController().navigate(R.id.expoListFragment, bundle, navOptions.options)
    }

    // Hide the soft keyboard
    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun checkIfAdmin(): Boolean {
        if (username == "admin") {
            // check admin uuids
            when (deviceID) {
                "dd025b48b806cb6e" -> return true
                "65225ac2ab20ca8c" -> return true
            }
        }
        return false
    }
}
