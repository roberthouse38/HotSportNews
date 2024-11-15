package com.example.hotsportnews.ui.home

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Team(
    val id: Int,
    val name: String,
    val slug: String,
    val fullName: String,
    val primaryColor: String,
    val venueName: String,
    val venueCity: String,
    val venueCapacity: Int,
    var logoUrl: String? = null // Tambahkan logoUrl sebagai properti dengan nilai default null
) : Parcelable
