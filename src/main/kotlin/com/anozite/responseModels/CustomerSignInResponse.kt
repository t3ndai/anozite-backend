package com.anozite.responseModels

data class CustomerSignInResponse(
    val authToken: String,
    val name: String
)