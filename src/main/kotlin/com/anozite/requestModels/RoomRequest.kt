package com.anozite.requestModels

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import org.hibernate.validator.*

enum class RoomType {
    STANDARD,
    DELUXE
}

class RoomRequest {

    @Positive(message = "has to be greater than 0")
    var sleepingCapacity: Int = 0
    @NotNull(message = "cannot be blank")
    lateinit var roomType: RoomType
    lateinit var facilities: Map<String, Any>
}