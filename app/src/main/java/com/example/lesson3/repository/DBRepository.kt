package com.example.lesson3.repository

import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface DBRepository {
    fun getOlympicGames(): Flow<List<OlympicGame>>
    suspend fun insertOlympicGame(olympicGame: OlympicGame)
    suspend fun updateOlympicGame(olympicGame: OlympicGame)
    suspend fun insertAllOlympicGames(olympicGameList: List<OlympicGame>)
    suspend fun deleteOlympicGame(olympicGame: OlympicGame)
    suspend fun deleteAllOlympicGames()

    fun getAllCompetitionTypes(): Flow<List<CompetitionType>>
    suspend fun getGameCompetitionTypes(storeId : UUID): List<CompetitionType>
    suspend fun insertCompetitionType(competitionType: CompetitionType)
    suspend fun deleteCompetitionType(competitionType: CompetitionType)
    suspend fun deleteGameCompetitionTypes(gameId: UUID)
    suspend fun deleteAllCompetitionTypes()

    fun getAllOlympicPlayers(): Flow<List<OlympicPlayer>>
    fun getCompetitionOlympicPlayers(departmentId: UUID): Flow<List<OlympicPlayer>>
    suspend fun insertOlympicPlayer(olympicPlayer: OlympicPlayer)
    suspend fun deleteOlympicPlayer(olympicPlayer: OlympicPlayer)
    suspend fun deleteCompetitionOlympicPlayers(competitionId: UUID)
    suspend fun deleteAllOlympicPlayers()
}
