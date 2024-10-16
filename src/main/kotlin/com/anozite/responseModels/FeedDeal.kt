package com.anozite.responseModels

import com.anozite.models.Hotel
import org.bson.types.ObjectId
import java.time.LocalDateTime

data class FeedDeal(
    val hotelPics: List<String>?,
    val hotel: FeedHotel,
    val price: Int,
    val checkIn: LocalDateTime,
    val checkOut: LocalDateTime,
    val expiresAt: LocalDateTime,
    val dealId: ObjectId,
    val rooms: Int?,
    val holdFee: Int
)