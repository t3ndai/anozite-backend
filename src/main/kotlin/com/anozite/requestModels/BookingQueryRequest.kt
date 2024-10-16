package com.anozite.requestModels

import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import java.time.LocalDate
import java.time.LocalDateTime

class BookingQueryRequest {

    @NotNull
    lateinit var checkIn: LocalDateTime
    @FutureOrPresent
    lateinit  var checkOut: LocalDateTime
    @PositiveOrZero(message = "positive only")
    var meetingRooms: Int = 0
    @PositiveOrZero(message = "positive only")
    var guests: Int = 0
    @PositiveOrZero(message = "positive only")
    var rooms: Int = 0
    @PositiveOrZero(message = "positive only")
    var seatingCapacity = 0
    @NotBlank(message = "city cannot be blank")
    lateinit var city: String
    var hotelId: String? = null
    var id: String? = null
}