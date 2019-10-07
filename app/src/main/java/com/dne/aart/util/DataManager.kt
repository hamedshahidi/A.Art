package com.dne.aart.util

import com.dne.aart.model.Expo

object DataManager {

    var expoList = listOf<Expo>()


    fun getExpoById(expoId: Int): Expo {
        return expoList.filter { expo -> expo.id == expoId }[0]
    }

}