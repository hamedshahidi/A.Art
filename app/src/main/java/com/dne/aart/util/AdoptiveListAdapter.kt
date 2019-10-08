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
import com.dne.aart.view.ArtListFragmentDirections
import com.dne.aart.view.ExpoListFragmentDirections
import com.dne.aart.view.TAG
import kotlinx.android.synthetic.main.list_item_art.view.*
import kotlinx.android.synthetic.main.list_item_expo.view.*

class AdoptiveListAdapter(context: Context, expoId: Int?) : RecyclerView.Adapter<Holder>() {

    val expoId = expoId
    private val context = context

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
        return if (expoId == null) {DataManager.expoList.count()}
        else {DataManager.expoList.filter { expo -> expo.id == expoId }[0].models.count()}
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val expo = DataManager.expoList[position]
        if ( expoId == null) {
            holder.view.tv_info_expo.text = "info: " + expo.info
            val imgId = context.resources.getIdentifier(
                expo.image_url, "drawable", context.packageName
            )
            holder.view.imgv_expo.setImageResource(imgId)
            //val imgPath = Uri.parse("android.resource://com.dne.aart/drawable/" + expo.image_url)
            //holder.view.imgv_expo.setImageURI(imgPath)
            holder.view.tv_title_expo.text = "title: " + expo.title
            val expoIdArg: Int = expo.id
            val btnOpenExpo = holder.view.findViewById<Button>(R.id.btn_open_expo)
            btnOpenExpo.setOnClickListener{
                val action =
                    ExpoListFragmentDirections.actionExpoListFragmentToExpoInfoFragment()
                action.expoId = expoIdArg
                holder.view.findNavController().navigate(action)
            }
        } else {
            val thisExpo = DataManager.expoList.filter { expo -> expo.id == expoId }[0]
            val model = thisExpo.models[position]
            Log.d(TAG,"position: $position")
            Log.d(TAG,"models.size: ${thisExpo.models.size}")
            holder.view.tv_model_name.text = model.title
            holder.view.tv_model_info.text = model.info
            val imgPath = Uri.parse("android.resource://com.dne.aart/drawable/"
                    + model.image_url)
            holder.view.imgv_model.setImageURI(imgPath)
            val modelIdArg = model.id
            val cellContainer =
                holder.view.findViewById<LinearLayout>(R.id.expo_cell_container)
            cellContainer.setOnClickListener {
                val action =
                    ArtListFragmentDirections.actionArtListFragmentToCamSceneFragment()
                action.modelId = model.id
                holder.view.findNavController().navigate(action)
            }
        }
    }
}

//private fun JSONArray.toMutableList(): MutableList<Any> = MutableList(length(), this::get)

class Holder(val view: View) : RecyclerView.ViewHolder(view)

