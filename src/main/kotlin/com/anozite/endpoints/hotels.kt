package com.anozite.endpoints

import at.favre.lib.crypto.bcrypt.BCrypt
import com.anozite.models.*
import com.anozite.requestModels.ApproveBookingRequestR
import com.anozite.requestModels.HotelRequest
import com.anozite.requestModels.HotelSignInRequest
import com.anozite.requestModels.UploadRequest
import com.anozite.responseModels.HotelHomeResponse
import com.anozite.responseModels.HotelResponse
import com.anozite.utils.ValidationResult
import com.anozite.utils.generateToken
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.*
import jakarta.ws.rs.core.UriBuilder
import jakarta.ws.rs.core.UriInfo
import io.quarkus.logging.Log
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.Valid
import jakarta.ws.rs.PATCH
import jakarta.ws.rs.core.SecurityContext
import org.bson.types.ObjectId
import org.eclipse.microprofile.jwt.JsonWebToken

import software.amazon.awssdk.services.s3.S3Client
import java.time.LocalDateTime


@Path("/hotels")
@Consumes(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
@Produces(MediaType.APPLICATION_JSON)
class HotelsResource {

    @Inject
    @field: Default
    lateinit var validator: Validator

    @Inject
    private lateinit  var jwt: JsonWebToken

    @Inject
    private lateinit var s3Client: S3Client


    @GET
    fun list() : List<Hotel> {
        return Hotel.listAll();
    }

    @POST
    fun create(hotelRequest: HotelRequest, @Context uriInfo: UriInfo): Response {
        // capture hotel event
        val violations: Set<ConstraintViolation<HotelRequest>> = validator.validate(hotelRequest)
        if (violations.isEmpty()) {
            // check email already in use
            val hotelResult = Hotel.findByEmail(hotelRequest.email)

            if (hotelResult != null) {
                return status(Status.FORBIDDEN).build()
            }

            val hotel = Hotel()
            hotel.name = hotelRequest.name
            hotel.email = hotelRequest.email
            hotel.locationCity = hotelRequest.locationCity
            hotel.passwordDigest = BCrypt.withDefaults().hashToString(12, hotelRequest.password.toCharArray())
            hotel.persist();

            // send verification email
            // onboarding

            val uriBuilder: UriBuilder = uriInfo.absolutePathBuilder
            uriBuilder.path(hotel.id.toString())
            return created(uriBuilder.build()).build()
        } else {
            val validationResult = ValidationResult(violations)
            return status(Status.BAD_REQUEST).entity(validationResult.messsage).build();
        }
    }

    @Path("sign-in")
    @POST
    fun signIn(hotelSignInRequest: HotelSignInRequest, @Context uriInfo: UriInfo): Response {
        val violations = validator.validate(hotelSignInRequest)
        if (violations.isEmpty()) {
            val hotel = Hotel.findByEmail(hotelSignInRequest.email)
            if (hotel != null) {
                val passwordVerification = BCrypt.verifyer().verify(hotelSignInRequest.password.toCharArray(), hotel.passwordDigest)
                if (!passwordVerification.verified) {
                    return status(Status.UNAUTHORIZED).build()
                }

                val authToken = generateToken(userId = hotel.id.toString(), arrayOf("hotel"))
                val hotelResponse = HotelResponse(name = hotel.name, city = hotel.locationCity, authToken)

                return status(Status.OK).entity(hotelResponse).build()
            }

            return status(Status.NOT_FOUND).build()

        } else {
            val validationResult = ValidationResult(violations)
            return status(Status.BAD_REQUEST).entity(validationResult.messsage).build()
        }
    }

    @Path("home")
    @GET
    @RolesAllowed("hotel")
    fun home(@Context ctx: SecurityContext): Response {
        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        // get deals by hotel
        val deals = Deal.findByHotel(hotelId)
        val groupDeals = GroupDeal.findByHotel(hotelId)
        val hotelResult = Hotel.findById(hotelId)

        // meeting rooms
        val meetingRooms = hotelResult?.meetingRooms?.map { roomId -> MeetingRoom.findById(roomId) }?.toList()
        val sleepingRooms = hotelResult?.sleepingRooms?.map { roomId -> SleepingRoom.findById(roomId) }?.toList()

        val hotelResponse = HotelHomeResponse(deals = deals, groupDeals = groupDeals, meetingRooms = meetingRooms, sleepingRooms = sleepingRooms)

        return status(200).entity(hotelResponse).build()

    }

    @Path("assets")
    @POST
    @RolesAllowed("hotel")
    fun uploadAssets(uploadRequest: UploadRequest, @Context ctx: SecurityContext): Response {


        if (uploadRequest.files.isEmpty()) {
            return status(Status.BAD_REQUEST).build()
        }

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        val hotelResult = Hotel.findById(hotelId)


        if (hotelResult != null) {
            hotelResult.pictures = uploadRequest.files as ArrayList<String>
            hotelResult.update()
        }

        return status(200).build()
    }

    @Path("bookingRequests")
    @GET
    @RolesAllowed("hotel")
    fun getBookings(@Context ctx: SecurityContext): Response {

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        val bookingRequestsResults = BookingRequest.findByHotel(hotelId)

        return status(Status.OK).entity(bookingRequestsResults).build()

    }

    @Path("approveBooking")
    @POST
    @RolesAllowed("hotel")
    fun approveBooking(@Valid approveBookingRequestR: ApproveBookingRequestR, @Context ctx: SecurityContext): Response {
        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val bookingRequestId = ObjectId(approveBookingRequestR.id)
        val bookingRequest = BookingRequest.findById(bookingRequestId) ?: return status(Status.NOT_ACCEPTABLE).build()

        bookingRequest.acceptedAt = LocalDateTime.now()
        bookingRequest.status = BookingRequestStatus.ACCEPTED
        bookingRequest.price = approveBookingRequestR.price
        bookingRequest.update()

        return status(Status.OK).build()
    }

}
