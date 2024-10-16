package com.anozite.models

import com.anozite.requestModels.RoomType
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

@MongoEntity(collection = "sleepingRooms")
class SleepingRoom: PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<SleepingRoom> {}

    @BsonId
    var id: ObjectId = ObjectId()
    var sleepingCapacity: Int = 0
    lateinit var facilities: Map<String, Any>
    lateinit var roomType: RoomType
    var bookedFrom: LocalDateTime? = null
    var bookedTo: LocalDateTime? = null
}