package com.anozite.requestModels

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import org.hibernate.validator.constraints.UniqueElements

class MeetingRoomRequest {
    @NotBlank(message = "name cannot be blank")
    lateinit var humanFriendlyName: String
    var internalSKU: String? = null
    @Positive(message = "number greater than Zero")
    var seatingCapacity: Int = 0
    lateinit var facilities: Map<String, Any>
}