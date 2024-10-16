package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@MongoEntity(collection = "meetingRooms")
class MeetingRoom: PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<MeetingRoom> {

    }

    @BsonId
    var id: ObjectId = ObjectId()
    lateinit var humanFriendlyName: String
    lateinit var internalSKU: String
    lateinit var facilities: Map<String, Any>
    var seatingCapacity: Int = 0
    var bookedFrom: LocalDateTime? = null
    var bookedTo: LocalDateTime? = null
}