package com.anozite.utils

import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.jwt.Claims;
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import kotlin.collections.HashSet

fun generateToken(userId: String, roles: Array<String>): String {
    return Jwt.issuer("https://anotize.com")
        .expiresAt(LocalDateTime.now().atZone(ZoneId.systemDefault()).plusDays(1).toEpochSecond())
        .upn(userId)
        .groups(HashSet(roles.asList()))
        .sign()
}