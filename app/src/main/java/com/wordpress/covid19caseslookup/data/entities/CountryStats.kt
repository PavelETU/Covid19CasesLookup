package com.wordpress.covid19caseslookup.data.entities

import com.google.gson.annotations.SerializedName

data class CountryStats(@SerializedName("Confirmed") val confirmed: Int,
                        @SerializedName("Deaths") val deaths: Int,
                        @SerializedName("Recovered") val recovered: Int,
                        @SerializedName("Date") val date: String)