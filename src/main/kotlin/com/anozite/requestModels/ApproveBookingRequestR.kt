package com.anozite.requestModels

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

class ApproveBookingRequestR {
    @Positive
    var price: Double = 0.0
    @NotNull
    lateinit var id: String
}