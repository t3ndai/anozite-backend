package com.anozite.requestModels

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

class CustomerSignInRequest {
    @NotBlank
    @Email
    lateinit var email: String
    @NotBlank
    @Length(min = 8)
    lateinit var password: String
}