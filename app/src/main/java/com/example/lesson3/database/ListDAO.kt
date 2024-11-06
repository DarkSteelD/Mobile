package com.example.lesson3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.data.User
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ListDAO {
        @Query("SELECT * FROM olympic_games order by name")
        fun getOlympicGames(): Flow<List<OlympicGame>>

        @Insert(entity = OlympicGame::class, onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertOlympicGame(olympicGame: OlympicGame)

        @Update(entity = OlympicGame::class)
        suspend fun updateOlympicGame(olympicGame: OlympicGame)

        @Insert(entity = OlympicGame::class, onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAllOlympicGames(olympicGameList: List<OlympicGame>)

        @Delete(entity = OlympicGame::class)
        suspend fun deleteOlympicGame(olympicGame: OlympicGame)

        @Query("DELETE FROM olympic_games")
        suspend fun deleteAllOlympicGames()

        @Query("SELECT * FROM competition_types")
        fun getAllCompetitionTypes(): Flow<List<CompetitionType>>

        @Query("SELECT * FROM competition_types WHERE game_id=:gameId")
        suspend fun getGameCompetitionTypes(gameId : UUID): List<CompetitionType>

        @Insert(entity = CompetitionType::class, onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertCompetitionType(competitionType: CompetitionType)

        @Delete(entity = CompetitionType::class)
        suspend fun deleteCompetitionType(competitionType: CompetitionType)

        @Query("DELETE FROM competition_types WHERE game_id=:gameId")
        suspend fun deleteGameCompetitionTypes(gameId: UUID)

        @Query("DELETE FROM competition_types")
        suspend fun deleteAllCompetitionTypes()

        @Query("SELECT * FROM olympic_players")
        fun getAllOlympicPlayers(): Flow<List<OlympicPlayer>>

        @Query("SELECT * FROM olympic_players WHERE competition_id=:competitionId")
        fun getCompetitionOlympicPlayers(competitionId: UUID): Flow<List<OlympicPlayer>>

        @Insert(entity = OlympicPlayer::class, onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertOlympicPlayer(olympicPlayer: OlympicPlayer)

        @Delete(entity = OlympicPlayer::class)
        suspend fun deleteOlympicPlayer(olympicPlayer: OlympicPlayer)

        @Query("DELETE FROM olympic_players WHERE competition_id=:competitionId")
        suspend fun deleteCompetitionOlympicPlayers(competitionId: UUID)

        @Query("DELETE FROM olympic_players")
        suspend fun deleteAllOlympicPlayers()

        @Query("SELECT * FROM users")
        fun getAllUsers(): Flow<List<User>>

        @Query("SELECT * FROM users WHERE id=:userId")
        fun getUserById(userId: UUID): Flow<User>

        @Insert(entity = User::class, onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertUser(user: User)

        @Delete(entity = User::class)
        suspend fun deleteUser(user: User)

        @Query("DELETE FROM users")
        suspend fun deleteAllUsers()
}
