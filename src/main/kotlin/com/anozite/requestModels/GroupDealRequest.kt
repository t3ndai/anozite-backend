package com.anozite.requestModels

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

class GroupDealRequest {
    lateinit var availableFrom: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var expiresAt: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var checkIn: LocalDateTime
    @Future(message = "date must be in the future")
    lateinit var checkOut: LocalDateTime
    @Positive
    var price: Double = 0.0
    @Positive
    var rooms: Int = 0
}