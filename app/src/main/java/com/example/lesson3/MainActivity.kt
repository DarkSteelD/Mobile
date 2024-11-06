package com.example.lesson3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.addCallback
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.fragments.OlympicGameFragment
import com.example.lesson3.fragments.LoginFragment
import com.example.lesson3.fragments.CompetitionTypeFragment
import com.example.lesson3.fragments.OlympicPlayerInputFragment
import com.example.lesson3.fragments.RegistrationFragment
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository

class MainActivity : AppCompatActivity(), MainActivityCallbacks {
    interface Edit {
        fun append()
        fun update()
        fun delete()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MyApplication.isAdmin.observeForever {
            updateMenu(activeFragment)
        }

        onBackPressedDispatcher.addCallback(this){
            if(supportFragmentManager.backStackEntryCount>0){
                supportFragmentManager.popBackStack()
                when (activeFragment){
                    NamesOfFragment.OLYMPIC_GAME->{
                        finish()
                    }
                    NamesOfFragment.OLYMPIC_COMPETITION->{
                        activeFragment=NamesOfFragment.OLYMPIC_GAME
                    }
                    NamesOfFragment.OLYMPIC_PLAYER->{
                        activeFragment=NamesOfFragment.OLYMPIC_COMPETITION
                    }
                    NamesOfFragment.LOGIN->{
                        activeFragment=NamesOfFragment.OLYMPIC_GAME
                    }
                    NamesOfFragment.REGISTRATION->{
                        activeFragment=NamesOfFragment.OLYMPIC_GAME
                    }
                    else ->{}
                }
                updateMenu(activeFragment)
            }
            else{
                finish()
            }
        }
        showFragment(activeFragment, null)
    }

    private var _miAppendGame: MenuItem ?= null
    private var _miUpdateGame: MenuItem ?= null
    private var _miDeleteGame: MenuItem ?= null
    private var _miAppendCompetition: MenuItem ?= null
    private var _miUpdateCompetition: MenuItem ?= null
    private var _miDeleteCompetition: MenuItem ?= null
    private var _miSortName: MenuItem ?= null
    private var _miSortCountry: MenuItem ?= null
    private var _miSortRanking: MenuItem ?= null
    private var _miSignup: MenuItem ?= null
    private var _miLogin: MenuItem ?= null
    private var _miExit: MenuItem ?= null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        _miAppendGame = menu?.findItem(R.id.miAppendGame)
        _miUpdateGame = menu?.findItem(R.id.miUpdateGame)
        _miDeleteGame = menu?.findItem(R.id.miDeleteGame)
        _miAppendCompetition = menu?.findItem(R.id.miAppendCompetition)
        _miUpdateCompetition = menu?.findItem(R.id.miUpdateCompetition)
        _miDeleteCompetition = menu?.findItem(R.id.miDeleteCompetition)

        _miSortName = menu?.findItem(R.id.miSortName)
        _miSortCountry = menu?.findItem(R.id.miSortCountry)
        _miSortRanking = menu?.findItem(R.id.miSortRanking)

        _miSignup = menu?.findItem(R.id.miSignup)
        _miLogin = menu?.findItem(R.id.miLogin)
        _miExit = menu?.findItem(R.id.miExit)
        updateMenu(activeFragment)
        return true
    }

    var activeFragment : NamesOfFragment=NamesOfFragment.OLYMPIC_GAME

    private fun updateMenu(fragmentType: NamesOfFragment){
        val isUserLoggedIn: Boolean = MyApplication.isAdmin.value == true

        _miAppendGame?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_GAME && isUserLoggedIn)
        _miUpdateGame?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_GAME && isUserLoggedIn)
        _miDeleteGame?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_GAME && isUserLoggedIn)
        _miAppendCompetition?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_COMPETITION && isUserLoggedIn)
        _miUpdateCompetition?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_COMPETITION && isUserLoggedIn)
        _miDeleteCompetition?.isVisible = (
                fragmentType==NamesOfFragment.OLYMPIC_COMPETITION && isUserLoggedIn)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.miAppendGame -> {
                val fedit: Edit = OlympicGameFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateGame -> {
                val fedit: Edit = OlympicGameFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteGame -> {
                val fedit: Edit = OlympicGameFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miAppendCompetition -> {
                val fedit: Edit = CompetitionTypeFragment.getInstance()
                fedit.append()
                true
            }
            R.id.miUpdateCompetition -> {
                val fedit: Edit = CompetitionTypeFragment.getInstance()
                fedit.update()
                true
            }
            R.id.miDeleteCompetition -> {
                val fedit: Edit = CompetitionTypeFragment.getInstance()
                fedit.delete()
                true
            }
            R.id.miSignup -> {
                showFragment(NamesOfFragment.REGISTRATION)
                true
            }

            R.id.miSortName -> {
                MyApplication.setSortByName(MyApplication.sortByName != true)
                MyApplication.setSortByRanking(null)
                MyApplication.setSortByCountry(null)
                AppRepository.getInstance().fetchPlayers()
                true
            }

            R.id.miSortRanking -> {
                MyApplication.setSortByRanking(MyApplication.sortByRanking != true)
                MyApplication.setSortByCountry(null)
                MyApplication.setSortByName(null)
                AppRepository.getInstance().fetchPlayers()
                true
            }

            R.id.miSortCountry -> {
                MyApplication.setSortByCountry(MyApplication.sortByCountry != true)
                MyApplication.setSortByRanking(null)
                MyApplication.setSortByName(null)
                AppRepository.getInstance().fetchPlayers()
                true
            }

            R.id.miLogin -> {
                showFragment(NamesOfFragment.LOGIN)
                true
            }
            R.id.miExit -> {
                AppRepository.getInstance().logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showFragment(fragmentType: NamesOfFragment, olympicPlayer: OlympicPlayer?){
        when(fragmentType){
            NamesOfFragment.OLYMPIC_GAME->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, OlympicGameFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.OLYMPIC_COMPETITION->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, CompetitionTypeFragment.getInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.OLYMPIC_PLAYER->{
                if(olympicPlayer!=null)
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fcMain, OlympicPlayerInputFragment.newInstance(olympicPlayer))
                        .addToBackStack(null)
                        .commit()
            }
            NamesOfFragment.REGISTRATION->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, RegistrationFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            NamesOfFragment.LOGIN->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fcMain, LoginFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            else -> {}
        }
        activeFragment=fragmentType
        updateMenu(fragmentType)
    }

    override fun newTitle(_title: String) {
        title = _title
    }

    override fun onStop() {
//        AppRepository.getInstance().saveData()
        super.onStop()
    }
}