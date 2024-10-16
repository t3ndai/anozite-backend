package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime


@MongoEntity(collection = "bookingQueries")
class BookingQuery: PanacheMongoEntityBase() {



    @BsonId
    var id: ObjectId = ObjectId()
    lateinit var checkIn: LocalDateTime
    lateinit var checkOut: LocalDateTime
    lateinit var customerId: ObjectId
    lateinit var city: String
    var meetingRooms = 0
    var guests = 0
    var rooms= 0
    var seatingCapacity = 0
    var created = LocalDateTime.now()
}