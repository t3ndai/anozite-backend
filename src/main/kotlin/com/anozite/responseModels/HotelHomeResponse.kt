package com.anozite.responseModels

import com.anozite.models.Deal
import com.anozite.models.GroupDeal
import com.anozite.models.MeetingRoom
import com.anozite.models.SleepingRoom

data class HotelHomeResponse(
    val deals: List<Deal>,
    val groupDeals: List<GroupDeal>,
    val meetingRooms: List<MeetingRoom?>?,
    val sleepingRooms: List<SleepingRoom?>?
)