package com.anozite.utils

import jakarta.validation.ConstraintViolation
import java.util.stream.Collectors

class ValidationResult<T>(violations: Set<ConstraintViolation<T>>) {

    var messsage: Map<String, String>

    init {
        this.messsage = violations.map { cv: ConstraintViolation<T> -> cv.propertyPath.toString() to cv.message }.toMap()
    }

}