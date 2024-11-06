package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(
    tableName = "olympic_players",
    foreignKeys = [
        ForeignKey(
            entity = CompetitionType::class,
            parentColumns = ["id"],
            childColumns = ["competition_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("id"),
        Index("competition_id"),
    ]
)

data class OlympicPlayer(
    @SerializedName("id") @PrimaryKey val id: UUID = UUID.randomUUID(),
    @SerializedName("name") var name: String = "",
    @SerializedName("country") var country: String = "",
    @SerializedName("ranking") var ranking: Int? = null,
    @SerializedName("score") var score: String = "",
    @SerializedName("age") var age: Int? = null,
    @SerializedName("sport") var sport: String = "",
    @SerializedName("height") var height: Int? = null,
    @SerializedName("weight") var weight: Int? = null,

    @SerializedName("competition_id")
    @ColumnInfo(name = "competition_id")
    var competitionId: UUID ?= null,
) {
    val shortContent
        get() =
            "$name ($country) место $ranking"
}

