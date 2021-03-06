package com.wordpress.covid19caseslookup.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class CountryStats(@PrimaryKey(autoGenerate = true) val id: Int = 0,
                        @SerializedName("Confirmed") val confirmed: Int,
                        @SerializedName("Deaths") val deaths: Int,
                        @SerializedName("Recovered") val recovered: Int,
                        @SerializedName("Date") val date: String)