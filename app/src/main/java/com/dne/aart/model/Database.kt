package com.dne.aart.model

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.dne.aart.view.TAG
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class Database(context: Context) {

    private val assetsManager: AssetManager = context.assets
    var allExpos = mutableListOf<Expo>()
    var allModels = mutableListOf<Model>()

    init {
        fetchData()
    }

    // Connect to database (read from local json file in this project)
    private fun readJsonFromAssets(): JSONObject? {
        // Read data from json file
        val charset: Charset = Charsets.UTF_8
        var json: String? = null
        try {
            val inStream: InputStream? = assetsManager.open("json_database.json")
            val size = inStream?.available()
            if (size != null) {
                val buffer = ByteArray(size)
                inStream.read(buffer)
                inStream.close()
                json = String(buffer, charset)
            }
        } catch (pokemon: IOException) {
            pokemon.printStackTrace()
            return null
        }
        return JSONObject(json)
    }


    // Read exhibitions data if available
    fun fetchData() {
        val data = readJsonFromAssets()
        if (data != null) {
            allExpos = getAllExpos(data)
            allModels = getAllModels(data)
        }
    }


    fun getAllExpos(data: JSONObject): MutableList<Expo> {
        val expoArray = data.getJSONArray("all_expos")
        val expoList = mutableListOf<Expo>()
        for (index in 0 until expoArray.length()) {
            expoList.add(getExpo(expoArray.getJSONObject(index)))
        }
        Log.d("DBG", "all expos: $expoList")

        return expoList
    }

    fun getAllModels(data: JSONObject): MutableList<Model> {
        val modelArray = data.getJSONArray("all_models")
        val modelList = mutableListOf<Model>()
        for (index in 0 until modelArray.length()) {
            modelList.add(getModel(modelArray.getJSONObject(index)))
        }
        Log.d("DBG", "all models: $modelList")
        return modelList
    }

    fun getExpo(expoObject: JSONObject): Expo {
        val expoId = expoObject.getInt("id") ?: 0
        val expoTitle = expoObject.getString("title") ?: "No title"
        val expoInfo = expoObject.getString("info") ?: "No info"
        val expoImageUrl = expoObject.getString("image_url") ?: "No image"
        return Expo(
            id = expoId,
            title = expoTitle,
            info = expoInfo,
            image_url = expoImageUrl,
            location = getLocationFor(expoObject),
            models = getModelListFor(expoObject)
        )
    }

    fun getLocationFor(expo: JSONObject): Location {
        val expoLocation = expo.getJSONObject("location")
        val lat = expoLocation.getDouble("lat")
        val long = expoLocation.getDouble("long")
        return Location(
            lat = lat,
            long = long
        )
    }

    fun getModelListFor(expo: JSONObject): MutableList<Model> {
        val expoModels = expo.getJSONArray("models")
        val expoModelList = mutableListOf<Model>()
        for (index in 0 until expoModels.length()) {
            expoModelList.add(getModel(expoModels.getJSONObject(index)))
        }
        return expoModelList
    }

    fun getModel(modelObject: JSONObject): Model {
        val modelId = modelObject.getInt("id")
        val modelTitle = modelObject.getString("title")
        val modelInfo = modelObject.getString("info")
        val modelImageUrl = modelObject.getString("image_url")
        val modelFile = modelObject.getString("model_file")
        val modelAudio = modelObject.getString("audio")
        val modelScore = modelObject.getInt("score")
        val modelCloudAnchorId = modelObject.getString("cloud_anchor_id")
        return Model(
            id = modelId,
            title = modelTitle,
            info = modelInfo,
            image_url = modelImageUrl,
            model_file = modelFile,
            audio = modelAudio,
            score = modelScore,
            cloud_anchor_id = modelCloudAnchorId
        )
    }
    //private fun JSONArray.toMutableList(): MutableList<Any> = MutableList(length(), this::get)
}