package com.example.lesson3.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.repository.AppRepository

class OlympicPlayerViewModel : ViewModel() {
    var olympicPlayerList: MutableLiveData<List<OlympicPlayer>> = MutableLiveData()

    private var _olympicPlayer: OlympicPlayer?= null
    val olympicPlayer
        get() = _olympicPlayer

    var competitionType: CompetitionType? = null

    fun set_CompetitionType(competitionType: CompetitionType) {
        this.competitionType = competitionType
        AppRepository.getInstance().listOfOlympicPlayer.observeForever{
            olympicPlayerList.postValue(AppRepository.getInstance().listOfOlympicPlayer.value)
        }
        AppRepository.getInstance().olympicPlayer.observeForever{
            _olympicPlayer=it
        }
    }

    fun deletePlayers() {
        if(olympicPlayer!=null)
            AppRepository.getInstance().deleteOlympicPlayer(olympicPlayer!!)
    }

    fun appendPlayer(
        name: String,
        country: String,
        score: String,
        ranking: Int,
        weight: Int,
        height: Int,
        age: Int,
        sport: String
    ){
        val olympicPlayer = OlympicPlayer()
        olympicPlayer.name = name
        olympicPlayer.country = country
        olympicPlayer.score = score
        olympicPlayer.sport = sport
        olympicPlayer.ranking = ranking
        olympicPlayer.weight = weight
        olympicPlayer.height = height
        olympicPlayer.age = age
        olympicPlayer.competitionId = competitionType!!.id
        AppRepository.getInstance().addOlympicPlayer(olympicPlayer)
    }

    fun updatePlayer(name: String,
                     country: String,
                     score: String,
                     ranking: Int,
                     weight: Int,
                     age: Int,
                     height: Int,
                     sport: String){
        if (_olympicPlayer!=null){
            _olympicPlayer!!.name = name
            _olympicPlayer!!.country = country
            _olympicPlayer!!.score = score
            _olympicPlayer!!.ranking = ranking
            _olympicPlayer!!.weight = weight
            _olympicPlayer!!.age = age
            _olympicPlayer!!.height = height
            _olympicPlayer!!.sport = sport
            AppRepository.getInstance().updateOlympicPlayer(_olympicPlayer!!)
        } else {
            _olympicPlayer = OlympicPlayer(
                name = name,
                country = country,
                score = score,
                ranking = ranking,
                weight = weight,
                age = age,
                height = height,
                sport = sport
            )
            AppRepository.getInstance().addOlympicPlayer(_olympicPlayer!!)
        }
    }

    fun setCurrentPlayer(olympicPlayer: OlympicPlayer){
        AppRepository.getInstance().setCurrentOlympicPlayer(olympicPlayer)
    }

}