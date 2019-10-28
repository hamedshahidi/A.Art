package com.dne.aart.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dne.aart.R
import com.dne.aart.model.Expo
import com.dne.aart.model.Model
import com.dne.aart.view.ArtListFragmentDirections
import com.dne.aart.view.ExpoListFragmentDirections
import com.dne.aart.view.TAG
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.list_item_art.view.*
import kotlinx.android.synthetic.main.list_item_expo.view.*

class AdoptiveListAdapter(
    private val context: Context,
    val expoId: Int?,
    private val isAdmin: Boolean) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val cellRow = if (expoId == null) {
            layoutInflater.inflate(R.layout.list_item_expo, parent, false)
                    as LinearLayout
        } else {
            layoutInflater.inflate(R.layout.list_item_art, parent, false)
                    as LinearLayout
        }
        return Holder(cellRow)
    }

    override fun getItemCount(): Int {
        return if (expoId == null) {
            DataManager.expoList.count()
        } else {
            if (isAdmin) DataManager.allModels.count()
            else DataManager.expoList.filter { expo -> expo.id == expoId }[0].models.count()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        val view = holder.view

        // If there is no exhibition id, show all exhibitions
        if (expoId == null) {
            val expo = DataManager.expoList[position]
            populateCellWithExpoData(view, expo)

            val expoIdArg: Int = expo.id
            val btnOpenExpo = view.findViewById<Button>(R.id.btn_open_expo)
            btnOpenExpo.setOnClickListener {
                val action =
                    ExpoListFragmentDirections.actionExpoListFragmentToExpoInfoFragment()
                action.expoId = expoIdArg

                view.findNavController().navigate(action)
            }

        } else {
            // If exhibition id is available, show list of models
            var allModels: MutableList<Model>

            // If user is admin get all models from data, otherwise get only this expos models
            allModels = when {
                isAdmin -> DataManager.allModels.toMutableList()
                else -> {
                    val thisExpo = DataManager.expoList.filter { item -> item.id == expoId }[0]
                    thisExpo.models.toMutableList()
                }
            }

            val model = allModels[position]
            val cellContainer = view.findViewById<LinearLayout>(R.id.expo_cell_container)

            populateCellWithModelData(view, model, position)

            cellContainer.setOnClickListener {
                val action =
                    ArtListFragmentDirections.actionArtListFragmentToCamSceneFragment()
                action.modelId = model.id
                action.admin = isAdmin
                action.expoId = expoId
                view.findNavController().navigate(action)
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun populateCellWithExpoData(view: View, expo: Expo) {
        view.tv_info_expo.text = expo.info
        val imgId = context.resources.getIdentifier(
            expo.image_url, "drawable", context.packageName
        )
        view.imgv_expo.setImageResource(imgId)
        view.tv_title_expo.text = expo.title
        val expoLocation: LatLng = LatLng(expo.location.lat, expo.location.long)
        view.tv_distance_expo.text = LocationManager.getDistanceTo(expoLocation).toString() + "Km"
    }

    private fun populateCellWithModelData(view: View, model: Model, position: Int) {
        view.tv_model_name.text = model.title
        view.tv_model_info.text = model.info
        val imgPath = Uri.parse(
            "android.resource://com.dne.aart/drawable/"
                    + model.image_url
        )
        view.imgv_model.setImageURI(imgPath)
    }
}

class Holder(val view: View) : RecyclerView.ViewHolder(view)

