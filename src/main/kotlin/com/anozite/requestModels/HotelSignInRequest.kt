package com.anozite.requestModels

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class HotelSignInRequest {
    @NotBlank(message = "email cannot be blank")
    @Email
    lateinit var email: String
    @NotBlank(message = "password cannot be blank")
    @Length(min = 8)
    lateinit var password: String
}