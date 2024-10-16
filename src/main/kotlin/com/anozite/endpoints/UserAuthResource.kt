package com.anozite.endpoints

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anozite.models.User
import com.anozite.requestModels.UserAuthRequest
import com.anozite.responseModels.UserResponse
import com.anozite.utils.generateToken
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.*
import jakarta.ws.rs.core.UriInfo
import io.quarkus.logging.Log

@Path("users/auth")
class UserAuthResource {

    @POST
    fun create(@Valid userAuthRequest: UserAuthRequest, @Context uriInfo: UriInfo): Response {

        val userResult = User.findByEmail(userAuthRequest.email)

        if (userResult != null) {
            return status(Status.UNAUTHORIZED).build()
        }

        val user = User()
        user.email = userAuthRequest.email
        user.passwordDigest = BCrypt.withDefaults().hashToString(12, userAuthRequest.password.toCharArray())
        // reward 100 points on sign-up
        user.points = 100
        user.persist()

        // send verification email

        val uriBuilder = uriInfo.absolutePathBuilder
        uriBuilder.path(user.id.toString())

        return created(uriBuilder.build()).build()

    }

    @Path("sign-in")
    @POST
    fun signIn(@Valid userAuthRequest: UserAuthRequest): Response {
        val userResult = User.findByEmail(userAuthRequest.email) ?: return status(Status.NOT_FOUND).build()

        val passwordVerification = BCrypt.verifyer().verify(userAuthRequest.password.toCharArray(), userResult.passwordDigest)
        if (!passwordVerification.verified) {
            return status(Status.UNAUTHORIZED).build()
        }

        val authToken = generateToken(userId = userResult.id.toString(), roles = arrayOf("user"))
        val userResponse = UserResponse(authToken)

        return status(Status.OK).entity(userResponse).build()

    }


    // this should be removed before prod
    @POST
    @Path("remove-all")
    fun removeAll(): Response {
        User.deleteAll()
        return status(Status.OK).build()
    }



}