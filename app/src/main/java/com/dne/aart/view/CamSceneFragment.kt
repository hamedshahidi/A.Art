package com.dne.aart.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dne.aart.R


class CamSceneFragment : Fragment() {

    private var modelId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cam_scene, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fManager = childFragmentManager
        //fragment = fManager.findFragmentById(R.id.ar_container)
        try {
            var fragment = fManager.findFragmentById(R.id.ar_fragment_container)
            if (fragment == null) {
                fragment = CloudAnchorFragment()
                fManager.beginTransaction().add(R.id.ar_fragment_container, fragment).commit()

            }
        } catch (e: Exception){
            Log.d("DBG", e.message)
        }
    }
}
