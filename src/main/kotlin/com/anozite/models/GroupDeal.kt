package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

enum class DealType {
    GROUP,
    SINGLE
}

@MongoEntity(collection = "deals")
class GroupDeal: PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<GroupDeal> {
        fun findByHotel(hotelId: ObjectId) = find("hotel", hotelId).list()
    }

    @BsonId
    var id: ObjectId = ObjectId()
    var price: Double = 0.0
    var rooms: Int = 0
    lateinit var expiresAt: LocalDateTime
    lateinit var checkIn: LocalDateTime
    lateinit var checkOut: LocalDateTime
    lateinit var availableFrom: LocalDateTime
    lateinit var hotel: ObjectId
    val dealType: DealType = DealType.GROUP
}