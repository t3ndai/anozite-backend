package com.anozite.responseModels

import java.time.LocalDateTime

data class BookingHotel(
    val name: String,
    val images: ArrayList<String>?
)

data class BookingResponse(
    val city: String?,
    val price: Double,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime,
    val guests: Int,
    val rooms: Int,
    val flexFee: Double,
    val rewardPoints: Int,
    val hotel: BookingHotel?,
    val bookingRequestId: String
)

data class CustomerBookingsResponse(
    val acceptedBookings: ArrayList<BookingResponse>,
    val pendingBookings: ArrayList<BookingResponse>
)