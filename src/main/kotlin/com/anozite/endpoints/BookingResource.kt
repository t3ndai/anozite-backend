package com.anozite.endpoints

import com.anozite.models.*
import com.anozite.requestModels.BookingQueryRequest
import com.anozite.requestModels.BookingRequestR
import com.anozite.responseModels.*
import com.mongodb.client.model.Filters
import io.quarkus.logging.Log
import io.quarkus.mongodb.panache.kotlin.PanacheQuery
import io.quarkus.panache.common.Page
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.core.Response.status
import jakarta.ws.rs.core.SecurityContext
import jakarta.ws.rs.core.UriInfo
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.eclipse.microprofile.jwt.JsonWebToken

@Path("bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
class BookingResource {

    @Inject
    private lateinit  var jwt: JsonWebToken


    @RolesAllowed("customer")
    @Path("query")
    @POST
    fun query(bookingQueryRequest: BookingQueryRequest, @Context ctx: SecurityContext, uriInfo: UriInfo): Response {

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        if (bookingQueryRequest.checkOut.isBefore(bookingQueryRequest.checkOut)) {
            return status(Status.BAD_REQUEST).build()
        }

        val customerId = ObjectId(ctx.userPrincipal.name)

        val customer = Customer.findById(customerId) ?: return status(Status.NOT_ACCEPTABLE).build()


        val hotelFilter: Bson = Filters.and(Filters.regex("locationCity", bookingQueryRequest.city, "i"))
        val hotelQuery = Hotel.mongoCollection().find(hotelFilter)


        val hotels = ArrayList<BookingQueryHotel?>()

        for (hotelResult in hotelQuery) {
            val meetingRoomList = hotelResult.meetingRooms.map { meetingRoomId ->
                MeetingRoom.find("seatingCapacity >= ?1", bookingQueryRequest.seatingCapacity).firstResult()
            }.map { meetingRoom ->
                    meetingRoom?.let { BookingQueryMeetingRoom(name = meetingRoom.humanFriendlyName, seatingCapacity = meetingRoom.seatingCapacity, facilities = meetingRoom.facilities) }
            }

            val roomTypes = hotelResult.sleepingRooms.map { roomId ->
                SleepingRoom.findById(roomId)?.roomType
            }

            val meetingRooms =  meetingRoomList.toMutableList() as ArrayList<BookingQueryMeetingRoom?>


            val hotel = BookingQueryHotel(id = hotelResult.id.toString(), name = hotelResult.name, location = hotelResult.locationCity, meetingRooms = meetingRooms, images = hotelResult.pictures, roomTypes = roomTypes)
            hotels.add(hotel)
        }

        val bookingQuery = BookingQuery()
        bookingQuery.checkIn = bookingQueryRequest.checkIn
        bookingQuery.checkOut = bookingQueryRequest.checkOut
        bookingQuery.city = bookingQueryRequest.city
        bookingQuery.rooms = bookingQueryRequest.rooms
        bookingQuery.meetingRooms = bookingQueryRequest.meetingRooms
        bookingQuery.guests = bookingQueryRequest.guests
        bookingQuery.customerId = customer.id
        bookingQuery.seatingCapacity = bookingQueryRequest.seatingCapacity
        bookingQuery.persist()

        val bookingQueryResult = BookingQueryResult(bookingQuery = bookingQuery, hotels = hotels)

        // TODO - return search results
        return status(Status.OK).entity(bookingQueryResult).build()

    }

    @RolesAllowed("customer")
    @POST
    @Path("requestBooking")
    fun requestBooking(@Valid bookingRequestR: BookingRequestR, @Context ctx: SecurityContext): Response {

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val customerId = ObjectId(ctx.userPrincipal.name)

        val customer = Customer.findById(customerId) ?: return status(Status.NOT_ACCEPTABLE).build()

        val bookingRequest = BookingRequest()
        bookingRequest.bookingQueryId = ObjectId(bookingRequestR.id)
        bookingRequest.customerId = customer.id
        bookingRequest.hotelId = ObjectId(bookingRequestR.hotelId)
        bookingRequest.checkIn = bookingRequestR.checkIn
        bookingRequest.checkOut = bookingRequestR.checkOut
        bookingRequest.guests = bookingRequestR.guests
        bookingRequest.rooms = bookingRequestR.rooms
        bookingRequest.meetingRooms = bookingRequestR.meetingRooms
        bookingRequest.seatingCapacity = bookingRequestR.seatingCapacity

        bookingRequest.persist()

        // TODO
        return status(Status.OK).build()
    }

    @Path("getCustomerBookings")
    @GET
    @RolesAllowed("customer")
    fun getCustomerBookings(@Context ctx: SecurityContext): Response {

        if (ctx.userPrincipal.name != jwt.name) {
            return status(Status.UNAUTHORIZED).build()
        }

        val customerId = ObjectId(ctx.userPrincipal.name)

        Customer.findById(customerId) ?: return status(Status.NOT_ACCEPTABLE).build()


        val bookingRequests = BookingRequest.findByCustomer(customerId = customerId)

        val pendingBookingRequests = bookingRequests.filter { it -> it.status == BookingRequestStatus.PENDING }
        val acceptedBookingRequests = bookingRequests.filter { it -> it.status == BookingRequestStatus.ACCEPTED }
        val acceptedBookings = helperCreateBookingResponse(acceptedBookingRequests)
        val pendingBookings = helperCreateBookingResponse(pendingBookingRequests)

        val customerBookingsResponse = CustomerBookingsResponse(acceptedBookings = acceptedBookings, pendingBookings = pendingBookings)

        return status(Status.OK).entity(customerBookingsResponse).build()

    }


}

fun helperCreateBookingResponse(bookingRequests: List<BookingRequest>): ArrayList<BookingResponse> {
    val bookings = ArrayList<BookingResponse>()
    for (bookingRequest in bookingRequests) {
        val hotelResult = Hotel.findById(bookingRequest.hotelId)
        val bookingHotel = hotelResult?.let { BookingHotel(name = it.name, images = it.pictures ) }
        // TODO - calculate flex flee based on engine
        val flexFee = bookingRequest.price * 0.075
        // TODO - calculate reward points based on engine
        val rewardPoints = ((bookingRequest.price * 682)  / 150).toInt()
        val bookingResponse = BookingResponse(city = hotelResult?.locationCity, flexFee = flexFee, guests = bookingRequest.guests, rooms = bookingRequest.rooms, hotel = bookingHotel, price = bookingRequest.price, bookingRequestId = bookingRequest.id.toString(), checkOut = bookingRequest.checkOut, checkIn = bookingRequest.checkIn, rewardPoints = rewardPoints )
        bookings.add(bookingResponse)
    }
    return bookings
}