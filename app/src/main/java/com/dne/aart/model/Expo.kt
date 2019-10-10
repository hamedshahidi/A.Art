package com.dne.aart.model

import com.dne.aart.model.Model

data class Expo(
    val id: Int,
    val image_url: String,
    val info: String,
    val location: Location,
    val models: List<Model>,
    val title: String
)