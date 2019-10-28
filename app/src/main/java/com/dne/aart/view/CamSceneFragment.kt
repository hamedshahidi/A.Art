package com.dne.aart.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.dne.aart.R
import kotlinx.android.synthetic.main.fragment_cam_scene.*


class CamSceneFragment : Fragment() {

    private var expoId = 0
    private var modelId = 0
    private var isAdmin: Boolean = false

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val safeArgs: CamSceneFragmentArgs by navArgs()
        expoId = safeArgs.expoId
        modelId = safeArgs.modelId
        isAdmin = safeArgs.admin
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.dne.aart.R.layout.fragment_cam_scene, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isAdmin) bottomPanel.isVisible = false

        val fManager = childFragmentManager
        //fragment = fManager.findFragmentById(R.id.ar_container)
        try {
            var fragment = fManager.findFragmentById(com.dne.aart.R.id.ar_fragment_container)
            if (fragment == null) {
                fragment = CloudAnchorFragment()

                val bundle = Bundle()
                bundle.putInt("expoId", expoId)
                bundle.putInt("modelId", modelId)
                bundle.putBoolean("isAdmin", isAdmin)
                fragment.setArguments(bundle)
                fManager.beginTransaction().add(R.id.ar_fragment_container, fragment).commit()
            }
        } catch (e: Exception){
            Log.d("DBG", e.message)
        }
    }
}
