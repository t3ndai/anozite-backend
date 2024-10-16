package com.anozite.requestModels

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class HotelRequest {
    @NotBlank(message = "name cannot be blank")
    lateinit var name: String
    @NotBlank(message = "email cannot be blank")
    @Email
    lateinit var email: String
    @NotBlank(message = "location city cannot be blank")
    lateinit var locationCity: String
    @NotBlank(message = "password cannot be blank")
    @Length(min = 8)
    lateinit var password: String
}