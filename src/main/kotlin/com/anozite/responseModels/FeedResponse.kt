package com.anozite.responseModels

data class FeedResponse(
    val deals: List<FeedDeal>,
    val weekendDeals: List<FeedDeal>,
    val groupDeals: List<FeedDeal>,
)