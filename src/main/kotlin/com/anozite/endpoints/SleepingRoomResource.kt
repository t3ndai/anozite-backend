package com.anozite.endpoints

import com.anozite.models.Hotel
import com.anozite.models.SleepingRoom
import com.anozite.requestModels.RoomRequest
import jakarta.annotation.security.RolesAllowed
import jakarta.enterprise.inject.Default
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.validation.Validator
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.*
import jakarta.ws.rs.core.Response.*
import org.bson.types.ObjectId
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("hotels/rooms")
class SleepingRoomResource {

    @Inject
    @field: Default
    lateinit var validator: Validator

    @Inject
    private lateinit  var jwt: JsonWebToken

    @POST
    @RolesAllowed("hotel")
    fun create(@Valid roomRequest: RoomRequest, @Context ctx: SecurityContext, uriInfo: UriInfo): Response {

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        val hotelResult = Hotel.findById(hotelId)
        val sleepingRoom = SleepingRoom()

        sleepingRoom.roomType = roomRequest.roomType
        sleepingRoom.facilities = roomRequest.facilities
        sleepingRoom.sleepingCapacity = roomRequest.sleepingCapacity
        sleepingRoom.persist()

        if (hotelResult != null) {
            hotelResult.sleepingRooms.add(sleepingRoom.id)
            hotelResult.update()
        }

        val uriBuilder: UriBuilder = uriInfo.absolutePathBuilder
        uriBuilder.path(sleepingRoom.id.toString())
        return created(uriBuilder.build()).build()
    }

}