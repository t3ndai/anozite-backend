package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDateTime

enum class CustomerType {
    NONPROFIT,
    REGULAR
}

@MongoEntity(collection = "customers")
class Customer: PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<Customer> {
        fun findByEmail(email: String) = Customer.find("email", email).firstResult()
    }

    @BsonId
    var id: ObjectId = ObjectId()
    lateinit var name: String
    lateinit var customerType: CustomerType
    lateinit var email: String
    lateinit var passwordDigest: String
    val createdAt = LocalDateTime.now()
    var refundCredits: Int = 0
    var points: Int = 0
}