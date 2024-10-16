package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@MongoEntity(collection = "bookingRequests")
class BookingRequest: PanacheMongoEntityBase() {


    companion object: PanacheMongoCompanion<BookingRequest> {
        fun findByHotel(hotelId: ObjectId) = find("hotelId", hotelId).list()

        fun findByCustomer(customerId: ObjectId) = find("customerId", customerId).list()
    }



    @BsonId
    var id: ObjectId = ObjectId()
    lateinit var checkIn: LocalDateTime
    lateinit var checkOut: LocalDateTime
    lateinit var customerId: ObjectId
    lateinit var hotelId: ObjectId
    lateinit var bookingQueryId: ObjectId
    var price = 0.0
    var meetingRooms = 0
    var guests = 0
    var rooms= 0
    var seatingCapacity = 0
    var status: BookingRequestStatus = BookingRequestStatus.PENDING
    var created = LocalDateTime.now()
    var acceptedAt: LocalDateTime? = null
}

enum class BookingRequestStatus {
    PENDING,
    ACCEPTED
}