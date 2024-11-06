package com.example.lesson3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.data.User
import java.util.concurrent.Executors

@Database(
    entities = [OlympicGame::class,
                CompetitionType::class,
                OlympicPlayer::class,
                User::class
               ],
    version = 15,
    exportSchema = false
)
@TypeConverters(ListTypeConverters::class)

abstract class ListDatabase: RoomDatabase() {
    abstract fun listDAO(): ListDAO

    companion object{
        @Volatile
        private var INSTANCE: ListDatabase? = null

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context,
                ListDatabase::class.java,
                "list_database")
                .fallbackToDestructiveMigration()
                .build()

        fun getDatabase(context: Context): ListDatabase {
            val dbBuilder = Room.databaseBuilder(
                context,
                ListDatabase::class.java,
                "list_database")
                    .fallbackToDestructiveMigration()

            dbBuilder.setQueryCallback(RoomDatabase.QueryCallback { sqlQuery, bindArgs ->
                println("SQL Query: $sqlQuery SQL Args: $bindArgs")
            }, Executors.newSingleThreadExecutor())
            return dbBuilder.build()
        }
    }
}





