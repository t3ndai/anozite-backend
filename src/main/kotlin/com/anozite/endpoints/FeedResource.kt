package com.anozite.endpoints

import com.anozite.models.Deal
import com.anozite.models.DealType
import com.anozite.models.GroupDeal
import com.anozite.models.Hotel
import com.anozite.responseModels.FeedDeal
import com.anozite.responseModels.FeedHotel
import com.anozite.responseModels.FeedResponse
import io.quarkus.mongodb.panache.kotlin.PanacheQuery
import io.quarkus.panache.common.Page
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.Response.Status
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.collections.ArrayList

@Path("feed")
class FeedResource {

    @GET
    fun getFeed(): Response {

        // query deals
        val withinOneMonth = LocalDateTime.now().plusDays(30)
        val dealsExpiringSoonQuery: PanacheQuery<Deal> = Deal.find("expiresAt < ?1", withinOneMonth).page(Page.ofSize(25))
        val groupDealsQuery: PanacheQuery<GroupDeal> = GroupDeal.find("dealType", DealType.GROUP)
        val dealsFirstPage : List<Deal> = dealsExpiringSoonQuery.list()
        val groupDealsFirstPage: List<GroupDeal> = groupDealsQuery.list()

        val feedDeals: ArrayList<FeedDeal> = ArrayList()
        val weekendFeedDeals: ArrayList<FeedDeal> = ArrayList()
        val groupDeals: ArrayList<FeedDeal> = ArrayList()
        for (deal in dealsFirstPage) {
            val checkIn = deal.checkIn
            val checkOut = deal.checkOut
            val hotelResult = Hotel.findById(deal.hotel)
            val hotelPics = hotelResult?.pictures
            val holdFee = deal.price * 0.05
            val feedHotel = FeedHotel(name = hotelResult?.name ?: "", city = hotelResult?.locationCity ?: "", id = hotelResult?.id.toString())
            val feedDeal = FeedDeal(hotelPics = hotelPics, checkIn = checkIn, checkOut = checkOut, price = deal.price.toInt(), expiresAt =  deal.expiresAt, hotel = feedHotel, dealId = deal.id, rooms = null, holdFee = holdFee.toInt())

            if (checkIn.dayOfWeek == DayOfWeek.FRIDAY && checkOut.dayOfWeek == DayOfWeek.SUNDAY) {
                weekendFeedDeals.add(feedDeal)
            } else {
                feedDeals.add(feedDeal)
            }

        }

        for (deal in groupDealsFirstPage) {
            val checkIn = deal.checkIn
            val checkOut = deal.checkOut
            val hotelResult = Hotel.findById(deal.hotel)
            val hotelPics = hotelResult?.pictures
            val rooms = deal.rooms
            val holdFee = deal.price * 0.05
            val feedHotel = FeedHotel(name = hotelResult?.name ?: "", city = hotelResult?.locationCity ?: "", id = hotelResult?.id.toString())
            val feedDeal = FeedDeal(hotelPics = hotelPics, checkIn = checkIn, checkOut = checkOut, price = deal.price.toInt(), expiresAt =  deal.expiresAt, hotel = feedHotel, dealId = deal.id, rooms = rooms, holdFee = holdFee.toInt())

            groupDeals.add(feedDeal)

        }

        val feedResponse = FeedResponse(deals = feedDeals, weekendDeals = weekendFeedDeals, groupDeals = groupDeals)
        return Response.status(Status.OK).entity(feedResponse).build()
    }
}