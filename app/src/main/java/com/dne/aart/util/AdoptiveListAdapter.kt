package com.dne.aart.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.dne.aart.R
import com.dne.aart.model.Expo
import kotlinx.android.synthetic.main.list_item_art.view.*
import kotlinx.android.synthetic.main.list_item_expo.view.*

class AdoptiveListAdapter(flag: Int) : RecyclerView.Adapter<Holder>() {

    private val cellIdentifier = flag

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)


        val cellRow = if (cellIdentifier == 1) {
            layoutInflater.inflate(R.layout.list_item_expo, parent, false)
                as LinearLayout
        } else {
            layoutInflater.inflate(R.layout.list_item_art, parent, false)
                    as LinearLayout
        }
        return Holder(cellRow)
    }

    override fun getItemCount(): Int {
        return DataManager.expoList.count()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val expo = DataManager.expoList[position]
        if (cellIdentifier == 1) {
            holder.view.tv_id.text = "id: " + expo.id.toString()
            holder.view.tv_info.text = "info: " + expo.info
            holder.view.tv_image_url.text = "image url: " + expo.image_url
            holder.view.tv_title.text = "title: " + expo.title
            holder.view.tv_location_lat.text = "lat: " + expo.location.lat.toString()
            holder.view.tv_location_long.text = "long: " + expo.location.long.toString()
            holder.view.tv_models.text = "number of models: " + expo.models.size
        } else {
            // TODO() populate ui with art list item info
        }
    }
}
class Holder(val view: View) : RecyclerView.ViewHolder(view)

object DataManager {
    var expoList = listOf<Expo>()
}