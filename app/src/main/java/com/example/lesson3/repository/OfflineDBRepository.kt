package com.example.lesson3.repository

import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.database.ListDAO
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class OfflineDBRepository(val dao: ListDAO): DBRepository {
    override fun getOlympicGames(): Flow<List<OlympicGame>> = dao.getOlympicGames()
    override suspend fun insertOlympicGame(olympicGame: OlympicGame) = dao.insertOlympicGame(olympicGame)
    override suspend fun updateOlympicGame(olympicGame: OlympicGame) = dao.updateOlympicGame(olympicGame)
    override suspend fun insertAllOlympicGames(olympicGameList: List<OlympicGame>) = dao.insertAllOlympicGames(olympicGameList)
    override suspend fun deleteOlympicGame(olympicGame: OlympicGame) = dao.deleteOlympicGame(olympicGame)
    override suspend fun deleteAllOlympicGames() = dao.deleteAllOlympicGames()

    override fun getAllCompetitionTypes(): Flow<List<CompetitionType>> = dao.getAllCompetitionTypes()
    override suspend fun getGameCompetitionTypes(storeId : UUID): List<CompetitionType> = dao.getGameCompetitionTypes(storeId)
    override suspend fun insertCompetitionType(competitionType: CompetitionType) = dao.insertCompetitionType(competitionType)
    override suspend fun deleteCompetitionType(competitionType: CompetitionType) = dao.deleteCompetitionType(competitionType)
    override suspend fun deleteGameCompetitionTypes(gameId: UUID) = dao.deleteGameCompetitionTypes(gameId)
    override suspend fun deleteAllCompetitionTypes() = dao.deleteAllCompetitionTypes()

    override fun getAllOlympicPlayers(): Flow<List<OlympicPlayer>> =dao.getAllOlympicPlayers()
    override fun getCompetitionOlympicPlayers(departmentId : UUID): Flow<List<OlympicPlayer>> = dao.getCompetitionOlympicPlayers(departmentId)
    override suspend fun insertOlympicPlayer(olympicPlayer: OlympicPlayer) = dao.insertOlympicPlayer(olympicPlayer)
    override suspend fun deleteOlympicPlayer(olympicPlayer: OlympicPlayer) = dao.deleteOlympicPlayer(olympicPlayer)
    override suspend fun deleteCompetitionOlympicPlayers(competitionId: UUID) = dao.deleteCompetitionOlympicPlayers(competitionId)
    override suspend fun deleteAllOlympicPlayers() = dao.deleteAllOlympicPlayers()
}