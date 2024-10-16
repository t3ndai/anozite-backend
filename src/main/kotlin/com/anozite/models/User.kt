package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId


@MongoEntity(collection = "users")
class User: PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<User> {
        fun findByEmail(email: String) = User.find("email", email).firstResult()
    }

    @BsonId
    var id = ObjectId()
    lateinit  var email: String
    lateinit var passwordDigest: String
    var verified: Boolean = false
    // bookings made by user
    var bookings: ArrayList<ObjectId> = ArrayList()
    // reward points
    var points: Int = 0
}