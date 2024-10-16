package com.anozite.requestModels

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

class DealRequest {

    lateinit var availableFrom: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var expiresAt: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var checkIn: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var checkOut: LocalDateTime
    @Positive
    var price: Double = 0.0

}