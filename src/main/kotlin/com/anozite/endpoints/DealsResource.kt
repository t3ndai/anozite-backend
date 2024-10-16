package com.anozite.endpoints

import com.anozite.models.Deal
import com.anozite.models.GroupDeal
import com.anozite.models.Hotel
import com.anozite.requestModels.DealRequest
import com.anozite.requestModels.GroupDealRequest
import io.quarkus.logging.Log
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.*
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import org.bson.types.ObjectId
import org.eclipse.microprofile.jwt.JsonWebToken
import java.time.LocalDateTime

@Path("deals")
class DealsResource {

    @Inject
    private lateinit  var jwt: JsonWebToken

    @POST
    @RolesAllowed("hotel")
    fun create(@Valid dealsRequest: DealRequest, @Context ctx: SecurityContext, uriInfo: UriInfo): Response {

        if (dealsRequest.checkOut.isBefore(dealsRequest.checkIn)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (dealsRequest.expiresAt.isBefore(LocalDateTime.now())) {
            return status(Status.BAD_REQUEST).build()
        }

        if (dealsRequest.expiresAt.isBefore(dealsRequest.availableFrom)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (dealsRequest.expiresAt.isAfter(dealsRequest.checkIn)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        val hotel = Hotel.findById(hotelId) ?: return status(Status.NOT_ACCEPTABLE).build()

        val deal = Deal()
        deal.checkIn = dealsRequest.checkIn
        deal.checkOut = dealsRequest.checkOut
        deal.price = dealsRequest.price
        deal.expiresAt = dealsRequest.expiresAt
        deal.availableFrom = dealsRequest.availableFrom
        deal.hotel = hotel.id
        deal.persist()

        val uriBuilder = uriInfo.absolutePathBuilder
        uriBuilder.path(deal.id.toString())

        return created(uriBuilder.build()).build()

    }

    @POST
    @RolesAllowed("hotel")
    @Path("groups")
    fun createGroupDeal(groupDealRequest: GroupDealRequest, ctx: SecurityContext, uriInfo: UriInfo): Response {

        if (groupDealRequest.checkOut.isBefore(groupDealRequest.checkIn)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (groupDealRequest.expiresAt.isBefore(LocalDateTime.now())) {
            return status(Status.BAD_REQUEST).build()
        }

        if (groupDealRequest.expiresAt.isBefore(groupDealRequest.availableFrom)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (groupDealRequest.expiresAt.isAfter(groupDealRequest.checkIn)) {
            return status(Status.BAD_REQUEST).build()
        }

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val hotelId = ObjectId(ctx.userPrincipal.name)
        val hotel = Hotel.findById(hotelId) ?: return status(Status.NOT_ACCEPTABLE).build()

        val groupDeal = GroupDeal()
        groupDeal.price = groupDealRequest.price
        groupDeal.rooms = groupDealRequest.rooms
        groupDeal.checkIn = groupDealRequest.checkIn
        groupDeal.checkOut = groupDealRequest.checkOut
        groupDeal.availableFrom = groupDealRequest.availableFrom
        groupDeal.expiresAt = groupDealRequest.expiresAt
        groupDeal.hotel = hotel.id
        groupDeal.persist()

        val uriBuilder = uriInfo.absolutePathBuilder
        uriBuilder.path(groupDeal.id.toString())

        return created(uriBuilder.build()).build()

    }

}