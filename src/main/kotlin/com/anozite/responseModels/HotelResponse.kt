package com.anozite.responseModels

data class HotelResponse(
    val name: String,
    val city: String,
    var authToken: String?
)