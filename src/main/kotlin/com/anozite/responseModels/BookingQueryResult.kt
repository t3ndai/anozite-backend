package com.anozite.responseModels

import com.anozite.models.BookingQuery
import com.anozite.requestModels.RoomType

data class BookingQueryMeetingRoom(
    val name: String,
    val seatingCapacity: Int,
    val facilities: Map<String, Any>
)

data class BookingQueryHotel(
    val id: String,
    val name: String,
    val location: String,
    val meetingRooms: ArrayList<BookingQueryMeetingRoom?>,
    val images: ArrayList<String>,
    val roomTypes: List<RoomType?>
)

data class BookingQueryResult(
    val bookingQuery: BookingQuery,
    val hotels: ArrayList<BookingQueryHotel?>
)