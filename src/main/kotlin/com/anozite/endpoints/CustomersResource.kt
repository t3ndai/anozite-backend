package com.anozite.endpoints

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anozite.models.Customer
import com.anozite.models.CustomerType
import com.anozite.models.Hotel
import com.anozite.models.User
import com.anozite.requestModels.CustomerSignInRequest
import com.anozite.requestModels.CustomerSignUpRequest
import com.anozite.requestModels.UserAuthRequest
import com.anozite.responseModels.CustomerSignInResponse
import com.anozite.responseModels.HotelResponse
import com.anozite.responseModels.UserResponse
import com.anozite.utils.generateToken
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.*
import jakarta.ws.rs.core.UriBuilder
import jakarta.ws.rs.core.UriInfo
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("customers")
@Consumes(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
class CustomersResource {

    @Inject
    private lateinit var jwt: JsonWebToken

    @POST
    fun create(@Valid customerSignUpRequest: CustomerSignUpRequest, uriInfo: UriInfo): Response {
        val customerResult = Customer.findByEmail(customerSignUpRequest.email)

        if (customerResult != null) {
            return status(Status.FORBIDDEN).build()
        }

        val customer = Customer()
        customer.name = customerSignUpRequest.name
        customer.email = customerSignUpRequest.email
        customer.passwordDigest = BCrypt.withDefaults().hashToString(12, customerSignUpRequest.password.toCharArray())
        customer.customerType = if (customerSignUpRequest.isNonProfit) CustomerType.NONPROFIT else CustomerType.REGULAR
        customer.persist()

        // send verification email
        // onboarding

        val uriBuilder: UriBuilder = uriInfo.absolutePathBuilder
        uriBuilder.path(customer.id.toString())
        return created(uriBuilder.build()).build()

    }

    @Path("sign-in")
    @POST
    fun signIn(@Valid customerSignInRequest: CustomerSignInRequest): Response {

//        val hotel = Hotel.findByEmail(hotelSignInRequest.email)
//        if (hotel != null) {
//            val passwordVerification = BCrypt.verifyer().verify(hotelSignInRequest.password.toCharArray(), hotel.passwordDigest)
//            if (!passwordVerification.verified) {
//                return status(Status.UNAUTHORIZED).build()
//            }
//            val authToken = generateToken(userId = hotel.id.toString(), arrayOf("hotel"))
//            val hotelResponse = HotelResponse(name = hotel.name, city = hotel.locationCity, authToken)
//
//            return status(Status.OK).entity(hotelResponse).build()
//        }

        val customerResult =
            Customer.findByEmail(customerSignInRequest.email) ?: return status(Status.NOT_FOUND).build()

        val passwordVerification =
            BCrypt.verifyer().verify(customerSignInRequest.password.toCharArray(), customerResult.passwordDigest)
        if (!passwordVerification.verified) {
            return status(Status.UNAUTHORIZED).build()
        }

        // CUSTOMER SIGN IN ID
        println("SignInID: ${customerResult.id}")
        val authToken = generateToken(userId = customerResult.id.toString(), roles = arrayOf("customer"))
        val customerSignInResponse = CustomerSignInResponse(authToken = authToken, name = customerResult.name)

        return status(Status.OK).entity(customerSignInResponse).build()

    }

}
