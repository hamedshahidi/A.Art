package com.dne.aart.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dne.aart.MapsActivity
import com.dne.aart.R
import com.dne.aart.util.DataManager
import kotlinx.android.synthetic.main.fragment_expo_info.*

class ExpoInfoFragment : Fragment() {

    private var expoId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val safeArgs: ExpoInfoFragmentArgs by navArgs()
        expoId = safeArgs.expoId

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_expo_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val expo = DataManager.getExpoById(expoId)
        tv_title_expoinfo.text = expo.title
        tv_info_expoinfo.text = expo.info

        val lat = expo.location.lat
        val lon = expo.location.long

        btn_expand_map.setOnClickListener {
            val intent = Intent(activity!!.applicationContext, MapsActivity::class.java)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)

            startActivity(intent)
        }

        val btnSelectModel = view.findViewById<Button>(R.id.btn_select_model)
        btnSelectModel.setOnClickListener {
            val action =
                ExpoInfoFragmentDirections.actionExpoInfoFragmentToArtListFragment()
            action.expoId = expoId
            findNavController().navigate(action)
        }
    }
}
