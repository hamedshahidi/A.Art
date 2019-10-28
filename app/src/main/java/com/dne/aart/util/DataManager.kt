package com.dne.aart.util

import com.dne.aart.model.Expo
import com.dne.aart.model.Model

object DataManager {

    var expoList = listOf<Expo>()
    var allModels = listOf<Model>()

    fun getExpoById(expoId: Int): Expo {
        return expoList.filter { expo -> expo.id == expoId }[0]
    }
}