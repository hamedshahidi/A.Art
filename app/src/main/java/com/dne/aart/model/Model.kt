package com.dne.aart.model

data class Model(
    val audio: String,
    val cloud_anchor_id: String,
    val id: Int,
    val image_url: String,
    val info: String,
    val model_file: String,
    val score: Int,
    val title: String
)