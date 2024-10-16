package com.anozite.endpoints

import com.anozite.models.Hotel
import com.anozite.models.MeetingRoom
import com.anozite.requestModels.MeetingRoomRequest
import com.anozite.utils.ValidationResult
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validator
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.*
import jakarta.ws.rs.core.Response.*
import org.bson.types.ObjectId
import org.eclipse.microprofile.jwt.JsonWebToken
import io.quarkus.logging.Log
import jakarta.validation.Valid

@Path("hotels/meeting-rooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class MeetingRoomResource {

    @Inject
    @field: Default
    lateinit var validator: Validator

    @Inject
    private lateinit  var jwt: JsonWebToken

    @Path("hello")
    @GET
    @RolesAllowed("hotel")
    fun hello(): Response {
        return status(Status.OK).entity("hello").build()
    }

    @POST
    @RolesAllowed("hotel")
    fun create(@Valid meetingRoomRequest: MeetingRoomRequest, @Context ctx: SecurityContext, uriInfo: UriInfo): Response {


            if (ctx.userPrincipal.name != jwt.name) {
                return status(Status.UNAUTHORIZED).build()
            }

            val meetingRoom = MeetingRoom()
            val hotelId = ObjectId(ctx.userPrincipal.name)
            val hotelResult = Hotel.findById(hotelId)
            meetingRoom.humanFriendlyName = meetingRoomRequest.humanFriendlyName
            meetingRoom.seatingCapacity = meetingRoomRequest.seatingCapacity
            meetingRoom.internalSKU = if (meetingRoomRequest.internalSKU != null) meetingRoomRequest.internalSKU!! else meetingRoomRequest.humanFriendlyName
            meetingRoom.facilities = meetingRoomRequest.facilities
            meetingRoom.persist()

            if (hotelResult != null) {
                hotelResult.meetingRooms.add(meetingRoom.id)
                hotelResult.update()
            }

            val uriBuilder: UriBuilder = uriInfo.absolutePathBuilder
            uriBuilder.path(meetingRoom.id.toString())
            return created(uriBuilder.build()).build()

    }
}