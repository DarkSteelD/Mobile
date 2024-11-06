package com.example.lesson3.repository

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.lesson3.API.ListAPI
import com.example.lesson3.API.ListConnection
import com.example.lesson3.API.PostResult
import com.example.lesson3.MyApplication
import com.example.lesson3.data.OlympicGame
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.data.User
import com.example.lesson3.data.UserLogin
import com.example.lesson3.database.ListDatabase
import com.example.lesson3.myConsts.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.UUID

class AppRepository {
    companion object {
        private var INSTANCE: AppRepository?=null

        fun getInstance(): AppRepository {
            if (INSTANCE==null){
                INSTANCE= AppRepository()
            }
            return INSTANCE?:
            throw IllegalStateException("Репозиторий не инициализирован")
        }
    }

    var olympicGame: MutableLiveData<OlympicGame> = MutableLiveData()
    var competitionType: MutableLiveData<CompetitionType> = MutableLiveData()
    var previousTabCompetitionType: MutableLiveData<CompetitionType> = MutableLiveData()
    var olympicPlayer: MutableLiveData<OlympicPlayer> = MutableLiveData()


    fun getOlympicGamePosition(olympicGame: OlympicGame): Int=listOfOlympicGame.value?.indexOfFirst {
        it.id==olympicGame.id } ?:-1

    fun getOlympicGamePosition()=getOlympicGamePosition(olympicGame.value?: OlympicGame())

    fun setCurrentOlympicGame(position:Int){
        if (position<0 || (listOfOlympicGame.value?.size!! <= position))
            return setCurrentOlympicGame(listOfOlympicGame.value!![position])
    }

    fun setCurrentOlympicGame(_olympicGame: OlympicGame){
        olympicGame.postValue(_olympicGame)
    }

    fun loadData(){
        fetchOlympicGames()
    }

    fun getCompetitionTypesPosition(competitionType: CompetitionType): Int =
        listOfCompetitionType.value?.indexOfFirst {
            it.id==competitionType.id
        } ?:-1

    fun getCompetitionTypesPosition() = getCompetitionTypesPosition(
        competitionType.value?: CompetitionType()
    )

    fun setCurrentCompetitionType(position:Int){
        if (listOfCompetitionType.value==null || position<0 ||
            (listOfCompetitionType.value?.size!! <=position)) {
            return setCurrentCompetitionType(listOfCompetitionType.value!![position])
        }
    }

    fun setCurrentCompetitionType(_competitionType: CompetitionType){
        competitionType.postValue(_competitionType)
        Log.d(TAG, "AppRepo setCurrentDepartment: ${_competitionType}")
    }

    val gameCompetitionTypes
        get() = listOfCompetitionType.value?.filter {
            it.gameId == (olympicGame.value?.id ?: 0)
        } ?: listOf()

    val competitionOlympicPlayers
        get() = listOfOlympicPlayer.value?.filter {
            it.competitionId == (competitionType.value?.id ?: 0)
        } ?: listOf()

    fun getOlympicPlayersPosition(olympicPlayer: OlympicPlayer): Int =
        listOfOlympicPlayer.value?.indexOfFirst {
            it.id==olympicPlayer.id
        } ?:-1

    fun getOlympicPlayersPosition() = getOlympicPlayersPosition(
        olympicPlayer.value?: OlympicPlayer()
    )

    fun setCurrentOlympicPlayer(position:Int){
        if (listOfOlympicPlayer.value==null || position<0 ||
            (listOfOlympicPlayer.value?.size!! <=position))
            return setCurrentOlympicPlayer(listOfOlympicPlayer.value!![position])
    }

    fun setCurrentOlympicPlayer(_olympicPlayer: OlympicPlayer){
        olympicPlayer.postValue(_olympicPlayer)
    }

    fun getCompetitionOlympicPlayers(routeId: UUID) =
        (listOfOlympicPlayer.value?.filter
        { it.competitionId == routeId }?.sortedBy { it.shortContent } ?: listOf())

    private val listDB by lazy {OfflineDBRepository(ListDatabase.getDatabase(MyApplication.context).listDAO())}

    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    fun onDestroy(){
        myCoroutineScope.cancel()
    }

    val listOfOlympicGame: LiveData<List<OlympicGame>> = listDB.getOlympicGames().asLiveData()
    val listOfCompetitionType: LiveData<List<CompetitionType>> = listDB.getAllCompetitionTypes().asLiveData()
    var listOfOlympicPlayer: MutableLiveData<List<OlympicPlayer>> = MutableLiveData<List<OlympicPlayer>>().apply {
        listDB.getAllOlympicPlayers().map {
            this.value = it
        }
    }


    private var listAPI = ListConnection.getClient().create(ListAPI::class.java)

    fun fetchOlympicGames(){
        listAPI.getDepots().enqueue(object: Callback<List<OlympicGame>> {
            override fun onFailure(call: Call<List<OlympicGame>>, t :Throwable){
                Log.d(TAG,"Ошибка получения списка игр", t)
                showToast("Ошибка получения списка игр")
            }
            override fun onResponse(
                call : Call<List<OlympicGame>>,
                response: Response<List<OlympicGame>>
            ){
                if (response.code()==200){
                    val services = response.body()
                    val items = services?:emptyList()
                    Log.d(TAG,"Получен список игр $items")
                    myCoroutineScope.launch{
                        listDB.deleteAllOlympicGames()
                        for (f in items){
                            listDB.insertOlympicGame(f)
                        }
                    }
                } else {
                    showToast("Ошибка получения списка игр")
                }
            }
        })
    }

    private fun addOlympicGameQuery(olympicGame: OlympicGame){
        listAPI.postDepot(olympicGame)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) {
                        myCoroutineScope.launch {
                            listDB.dao.insertOlympicGame(olympicGame)
                        }
                    } else {
                        showToast("Ошибка добавления игры")
                    }
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка добавления игры",t)
                    showToast("Ошибка добавления игры")
                }
            })
    }

    private fun updateOlympicGameQuery(olympicGame: OlympicGame) {
        listAPI.updateDepot(olympicGame)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) {
                        myCoroutineScope.launch {
                            listDB.dao.updateOlympicGame(olympicGame)
                        }
                    } else {
                        showToast("Ошибка изменения игры")
                    }
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка изменения игры",t)
                    showToast("Ошибка изменения игры")
                }
            })
    }

    private fun deleteOlympicGameQuery(olympicGame: OlympicGame) {
        listAPI.deleteDepot(olympicGame.id.toString())
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response: Response<PostResult>){
                    if (response.code()==200) {
                        myCoroutineScope.launch {
                            listDB.dao.deleteOlympicGame(olympicGame)
                        }
                    } else {
                        showToast("Ошибка удаления игры")
                    }
                }
                override fun onFailure(call:Call<PostResult>,t: Throwable){
                    Log.d(TAG,"Ошибка удаления игры",t)
                    showToast("Ошибка удаления игры")
                }
            })
    }

    fun addOlympicGame(olympicGame: OlympicGame){
        addOlympicGameQuery(olympicGame)
    }

    fun updateOlympicGame(olympicGame: OlympicGame){
        updateOlympicGameQuery(olympicGame)
    }

    fun deleteOlympicGame(olympicGame: OlympicGame){
        deleteOlympicGameQuery(olympicGame)
    }

    fun fetchCompetitionTypes(routeId: String){

        val storeUUID = UUID.fromString(routeId)

        listAPI.getRoutes(routeId).enqueue(object: Callback<List<CompetitionType>> {
            override fun onResponse(
                call: Call<List<CompetitionType>>,
                response: Response<List<CompetitionType>>
            ) {
                if (response.code() == 200) {
                    val groups = response.body()
                    val items = groups ?: emptyList()
                    Log.d(TAG, "Получен список состязаний $items")
                    myCoroutineScope.launch {
                        listDB.deleteGameCompetitionTypes(storeUUID)
                        for (g in items) {
                            listDB.insertCompetitionType(g)
                        }
                    }
                } else {
                    showToast("Ошибка получения списка состязаний")
                }
            }

            override fun onFailure(call: Call<List<CompetitionType>>, t: Throwable) {
                Log.d(TAG, "Ошибка получения списка состязаний", t)
                showToast("Ошибка получения списка состязаний")
            }
        })
    }

    fun fetchCompetitionTypesLocal(): List<CompetitionType> {
        var deferredStoreDepartments: Deferred<List<CompetitionType>>;
        var storeDepartments: List<CompetitionType> = listOf();

        runBlocking {
            withTimeoutOrNull(1000) {
                deferredStoreDepartments = async {
                    listDB.dao.getGameCompetitionTypes(olympicGame.value!!.id)
                }

                storeDepartments = deferredStoreDepartments.await()
            }
        }

        return storeDepartments;
    }

    private fun addCompetitionTypeQuery(competitionType: CompetitionType) {
        listAPI.postRoute(competitionType)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchCompetitionTypes(competitionType.gameId.toString())
                    else showToast("Ошибка добавления состязания")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка добавления состязания", t)
                    showToast("Ошибка добавления состязания")
                }
            })
    }

    private fun updateCompetitionTypeQuery(competitionType: CompetitionType){
        listAPI.updateRoute(competitionType)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchCompetitionTypes(competitionType.gameId.toString())
                    else showToast("Ошибка изменения состязания")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка изменения состязания", t)
                    showToast("Ошибка изменения состязания")
                }
            })
    }

    private fun deleteCompetitionTypeQuery(competitionType: CompetitionType) {
        listAPI.deleteRoute(competitionType.id.toString())
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) fetchCompetitionTypes(competitionType.gameId.toString())
                    else showToast("Ошибка удаления состязания")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка удаления состязания", t)
                    showToast("Ошибка удаления состязания")
                }
            })
    }

    fun addCompetitionType(competitionType: CompetitionType){
        addCompetitionTypeQuery(competitionType)
    }

    fun updateCompetitionType(competitionType: CompetitionType){
        updateCompetitionTypeQuery(competitionType)
    }

    fun deleteCompetitionType(competitionType: CompetitionType){
        deleteCompetitionTypeQuery(competitionType)
    }

    fun fetchPlayers(){
        listAPI.getTrams(
            olympicGame.value?.id.toString(),
            competitionType.value?.id.toString()
        ).enqueue(object : Callback<List<OlympicPlayer>>{
            override fun onFailure(call:Call<List<OlympicPlayer>>, t : Throwable){
                Log.d(TAG,"Ошибка получения спортсменов",t)
                showToast("Ошибка получения спортсменов")
            }
            override fun onResponse(
                call:Call<List<OlympicPlayer>>,
                response: Response<List<OlympicPlayer>>
            ){
                if(response.code()==200){
                    val students = response.body()
                    var items = students?: emptyList()

                    if (MyApplication.sortByName == true) {
                        items = items.sortedBy { it.name }
                    } else if (MyApplication.sortByName == false) {
                        items = items.reversed()
                    }

                    if (MyApplication.sortByRanking == true) {
                        items = items.sortedBy { it.score }
                    } else if (MyApplication.sortByRanking == false) {
                        items = items.reversed()
                    }

                    if (MyApplication.sortByCountry == true) {
                        items = items.sortedBy { it.country }
                    } else if (MyApplication.sortByCountry == false) {
                        items = items.reversed()
                    }

                    myCoroutineScope.launch {
                        competitionType.value?.id?.let {
                            listDB.deleteCompetitionOlympicPlayers(it)
                        }
                        for (s in items){
                            listDB.insertOlympicPlayer(s)
                        }
                        listOfOlympicPlayer.postValue(items)
                    }
                } else {
                    showToast("Ошибка получения спортсменов")
                }
            }
        })
    }

    fun fetchPlayersLocal(): List<OlympicPlayer> {
        var competitionPlayers: List<OlympicPlayer> = listOf()
        if (competitionType.value !== null) {
            listDB.dao.getCompetitionOlympicPlayers(competitionType.value!!.id)
        }

        return competitionPlayers
    }

    private fun addOlympicPlayerQuery(olympicPlayer: OlympicPlayer){
        listAPI.postTram(olympicPlayer.id.toString(), olympicPlayer)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200)
                        fetchPlayers()
                    else showToast("Ошибка записи спортсмена")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка записи спортсмена", t.fillInStackTrace())
                    showToast("Ошибка записи спортсмена")
                }
            })
    }

    private fun updateOlympicPlayerQuery(olympicPlayer: OlympicPlayer) {
        listAPI.updateTram(competitionType.value?.id.toString(), olympicPlayer)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200)
                        fetchPlayers()
                    else showToast("Ошибка обновления спортсмена")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка обновления спортсмена", t.fillInStackTrace())
                    showToast("Ошибка обновления спортсмена")
                }
            })
    }

    private fun deleteOlympicPlayerQuery(olympicPlayer: OlympicPlayer) {
        listAPI.deleteTram(competitionType.value?.id.toString(), olympicPlayer.id.toString())
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200)
                        fetchPlayers()
                    else showToast("Ошибка удаления спортсмена")
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка удаления спортсмена", t.fillInStackTrace())
                    showToast("Ошибка удаления спортсмена")
                }
            })
    }

    fun addOlympicPlayer(olympicPlayer: OlympicPlayer){
        addOlympicPlayerQuery(olympicPlayer)
    }

    fun updateOlympicPlayer(olympicPlayer: OlympicPlayer){
        updateOlympicPlayerQuery(olympicPlayer)
    }

    fun deleteOlympicPlayer(olympicPlayer: OlympicPlayer){
        deleteOlympicPlayerQuery(olympicPlayer)
    }

    private fun registrationQuery(user: User) {
        listAPI.registration(user)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) {
                        MyApplication.setIsAdmin(true)
                    } else {
                        showToast("Ошибка регистрации")
                        MyApplication.setIsAdmin(false)
                    }
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка регистрации", t.fillInStackTrace())
                    showToast("Ошибка регистрации")
                    MyApplication.setIsAdmin(false)
                }
            })
    }

    private fun loginQuery(user: UserLogin) {
        listAPI.login(user)
            .enqueue(object : Callback<PostResult>{
                override fun onResponse(call:Call<PostResult>,response:Response<PostResult>){
                    if (response.code()==200) {
                        MyApplication.setIsAdmin(true)
                    } else {
                        showToast("Ошибка входа")
                        MyApplication.setIsAdmin(false)
                    }
                }
                override fun onFailure(call:Call<PostResult>,t:Throwable){
                    Log.d(TAG,"Ошибка входа", t.fillInStackTrace())
                    showToast("Ошибка входа")
                    MyApplication.setIsAdmin(false)
                }
            })
    }

    fun registration(user: User) {
        registrationQuery(user)

    }

    fun login(user: UserLogin) {
        loginQuery(user)
    }

    fun logout() {
        MyApplication.setIsAdmin(false)
    }

    private fun showToast(text: String) {
        Toast.makeText(MyApplication.context, text, Toast.LENGTH_LONG).show()
    }

}





















