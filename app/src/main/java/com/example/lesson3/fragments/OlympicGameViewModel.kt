package com.example.lesson3.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.OlympicGame
import com.example.lesson3.repository.AppRepository

class OlympicGameViewModel : ViewModel() {

    var olympicGameList: LiveData<List<OlympicGame>> =
        AppRepository.getInstance().listOfOlympicGame

    private var _olympicGame : OlympicGame? = OlympicGame()
    val olympicGame
        get()=_olympicGame

    init {
        AppRepository.getInstance().olympicGame.observeForever {
            _olympicGame = it
        }
    }

    fun deleteGame(){
        if (olympicGame != null)
            AppRepository.getInstance().deleteOlympicGame(olympicGame!!)
    }

    fun appendGame(gameName: String){
        val olympicGame=OlympicGame()
        olympicGame.name = gameName
        AppRepository.getInstance().addOlympicGame(olympicGame)
    }

    fun updateGame(gameName: String){
        if (_olympicGame != null){
            _olympicGame!!.name = gameName
            AppRepository.getInstance().updateOlympicGame(_olympicGame!!)
        }
    }

    fun setGame(olympicGame: OlympicGame){
        AppRepository.getInstance().setCurrentOlympicGame(olympicGame)
    }
}






