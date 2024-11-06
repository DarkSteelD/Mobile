package com.example.lesson3.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.repository.AppRepository

class CompetitionTypeViewModel : ViewModel() {

    var competitionTypeList: MutableLiveData<List<CompetitionType>> = MutableLiveData()
    var olympicPlayerList: MutableLiveData<List<OlympicPlayer>> = MutableLiveData()
    private var _competitionType : CompetitionType? = null
    val competitionType
        get()=_competitionType

    init {

        AppRepository.getInstance().listOfOlympicPlayer.observeForever {
            olympicPlayerList.postValue(
                AppRepository.getInstance().competitionOlympicPlayers
            )
        }

        AppRepository.getInstance().listOfCompetitionType.observeForever {
            competitionTypeList.postValue(
                AppRepository.getInstance().gameCompetitionTypes
            )
        }

        AppRepository.getInstance().competitionType.observeForever {
            _competitionType = it
            if (checkLocalPlayers()) {
                olympicPlayerList.postValue(
                    AppRepository.getInstance().competitionOlympicPlayers
                )
            } else {
                AppRepository.getInstance().fetchPlayers()
            }
        }

    }

    fun checkLocalPlayers(): Boolean {
        return AppRepository.getInstance().fetchPlayersLocal().isNotEmpty()
    }

    fun checkLocalCompetitionTypes(): Boolean {
        val competitionTypes = AppRepository.getInstance().fetchCompetitionTypesLocal()

        return competitionTypes.isNotEmpty()
    }

    fun deleteCompetitionType(){
        if(competitionType != null)
            AppRepository.getInstance().deleteCompetitionType(competitionType!!)
    }

    fun appendCompetitionType(routeName: String){
        val competitionType=CompetitionType()
        competitionType.name = routeName
        competitionType.gameId = AppRepository.getInstance().olympicGame.value?.id
        AppRepository.getInstance().addCompetitionType(competitionType)
    }

    fun updateCompetitionType(departmentName: String){
        if (_competitionType!=null){
            _competitionType!!.name = departmentName
            AppRepository.getInstance().updateCompetitionType(_competitionType!!)
        }
    }

    fun setCurrentCompetitionType(position: Int){
        if ((competitionTypeList.value?.size ?: 0)>position)
            competitionTypeList.value?.let{
                AppRepository.getInstance().setCurrentCompetitionType(it.get(position))
            }
    }

    fun setCurrentCompetitionType(competitionType: CompetitionType){
        AppRepository.getInstance().setCurrentCompetitionType(competitionType)
    }

    val previousCompetitionType
        get() = AppRepository.getInstance().previousTabCompetitionType.value

    val getCompetitionTypePosition
        get() = competitionType?.let { AppRepository.getInstance().getCompetitionTypesPosition(it) } ?: -1

    val competitionTypeValue
        get()=AppRepository.getInstance().competitionType.value
}