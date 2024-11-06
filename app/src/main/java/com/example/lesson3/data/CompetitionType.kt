package com.example.lesson3.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.UUID

@Entity(tableName = "competition_types",
        indices = [Index("id"),Index("game_id")],
        foreignKeys = [
            ForeignKey(
                entity = OlympicGame::class,
                parentColumns = ["id"],
                childColumns = ["game_id"],
                onDelete = ForeignKey.CASCADE
            )
        ]
)

data class CompetitionType(
    @SerializedName("id") @PrimaryKey var id: UUID = UUID.randomUUID(),
    @SerializedName("name") @ColumnInfo(name = "name") var name: String = "",
    @SerializedName("game_id") @ColumnInfo(name = "game_id") var gameId: UUID ?= null
)
