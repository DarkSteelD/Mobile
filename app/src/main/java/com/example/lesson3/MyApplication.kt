package com.example.lesson3

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.lesson3.repository.AppRepository

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AppRepository.getInstance().loadData()
    }

    init {
        instance = this
    }

    companion object {
        private var instance: MyApplication? = null

        private var _isAdmin: MutableLiveData<Boolean> = MutableLiveData()
        private var _sortByName: Boolean? = null
        private var _sortByCountry: Boolean? = null
        private var _sortByRanking: Boolean? = null


        val sortByName
            get() = _sortByName

        val sortByCountry
            get() = _sortByCountry

        val sortByRanking
            get() = _sortByRanking

        val isAdmin
            get() = _isAdmin

        fun setSortByName(value: Boolean?) {
            _sortByName = value
        }

        fun setSortByCountry(value: Boolean?) {
            _sortByCountry = value
        }

        fun setSortByRanking(value: Boolean?) {
            _sortByRanking = value
        }

        fun setIsAdmin(value: Boolean) {
            _isAdmin.value = value
        }

        val context
            get()= applicationContext()

        private fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
    
}