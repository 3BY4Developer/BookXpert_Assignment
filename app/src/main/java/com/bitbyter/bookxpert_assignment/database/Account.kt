package com.bitbyter.bookxpert_assignment.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "accounts")
@Serializable
data class AccountEntity(
    @SerialName("actid") @PrimaryKey val actid: Int,
    @SerialName("ActName") val actName: String,
    var imageUri: String? = null,
    var alternateName: String? = null

)
