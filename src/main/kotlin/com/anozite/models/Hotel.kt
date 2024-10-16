package com.anozite.models

import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank;
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.ChronoLocalDate
import java.time.chrono.ChronoLocalDateTime
import java.time.chrono.Chronology

@MongoEntity(collection = "hotels")
class Hotel : PanacheMongoEntityBase() {

    companion object: PanacheMongoCompanion<Hotel> {
        fun findByEmail(email: String) = find("email", email).firstResult()
    }

    @BsonId
    var id: ObjectId = ObjectId()
    @NotBlank(message = "name cannot be blank")
    lateinit var name: String
    @NotBlank(message = "email cannot be blank")
    @Email
    lateinit var email: String
    @NotBlank(message = "location city cannot be blank")
    lateinit var locationCity: String
    lateinit var passwordDigest: String
    var verified: Boolean = false
    var emailVerified: Boolean = false
    var onboardingComplete: Boolean = false
    var createdAt: LocalDateTime = LocalDateTime.now();
    // meetingRooms embedded
    var meetingRooms: ArrayList<ObjectId> = ArrayList()
    // sleepingRooms embedded
    var sleepingRooms: ArrayList<ObjectId> = ArrayList()
    var pictures: ArrayList<String> = ArrayList()
}