package com.example.lesson3.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.databinding.OlympicPlayerInputFragmentBinding
import com.example.lesson3.myConsts
import com.example.lesson3.repository.AppRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val ARG_PARAM1 = "student_param"

class OlympicPlayerInputFragment : Fragment() {
    private lateinit var olympicPlayer: OlympicPlayer
    private lateinit var _binding : OlympicPlayerInputFragmentBinding

    val binding
        get()=_binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
            if (param1==null)
                olympicPlayer=OlympicPlayer()
            else{
                val paramType= object : TypeToken<OlympicPlayer>(){}.type
                olympicPlayer = Gson().fromJson<OlympicPlayer>(param1, paramType)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OlympicPlayerInputFragmentBinding.inflate(inflater,container,false)
        Log.d(myConsts.TAG, "OnCreateView Order: ${AppRepository.getInstance().olympicPlayer.value}")

        Log.d(myConsts.TAG, "AppRepo StoreDepartment: ${
            AppRepository.getInstance().competitionType.value
                }" +
                "\nAppRepo StoreDepartments: ${
                    AppRepository.getInstance().gameCompetitionTypes
                }")

        setFocusChangeListenersOnFields()

        binding.etPlayerName.setText(olympicPlayer.name)
        binding.etPlayerCountry.setText(olympicPlayer.country)
        binding.etPlayerScore.setText(olympicPlayer.score)
        binding.etPlayerSport.setText(olympicPlayer.sport)

        binding.etPlayerRanking.setText(
            if (olympicPlayer.ranking !== null)
                olympicPlayer.ranking.toString()
            else ""
        )

        binding.etPlayerAge.setText(
            if (olympicPlayer.age !== null)
                olympicPlayer.age.toString()
            else ""
        )

        binding.etPlayerHeight.setText(
            if (olympicPlayer.height !== null)
                olympicPlayer.height.toString()
            else ""
        )

        binding.etPlayerWeight.setText(
            if (olympicPlayer.weight !== null)
                olympicPlayer.weight.toString()
            else ""
        )

        binding.btCancel.setOnClickListener{
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btSave.setOnClickListener {
            if (checkFields()) {
                olympicPlayer.name = binding.etPlayerName.text.toString()
                olympicPlayer.country = binding.etPlayerCountry.text.toString()
                olympicPlayer.score = binding.etPlayerScore.text.toString()
                olympicPlayer.ranking = binding.etPlayerRanking.text.toString().toInt()
                olympicPlayer.weight = binding.etPlayerWeight.text.toString().toInt()
                olympicPlayer.sport = binding.etPlayerSport.text.toString()
                olympicPlayer.height = binding.etPlayerHeight.text.toString().toInt()
                olympicPlayer.age = binding.etPlayerAge.text.toString().toInt()

                if (AppRepository.getInstance().olympicPlayer.value == null)
                    AppRepository.getInstance().addOlympicPlayer(olympicPlayer)
                else
                    AppRepository.getInstance().updateOlympicPlayer(olympicPlayer)
                AppRepository.getInstance().olympicPlayer.postValue(null)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        return binding.root
    }


    private fun setFocusChangeListenersOnFields() {
        val fields: List<EditText> = listOf(
            binding.etPlayerName,
            binding.etPlayerCountry,
            binding.etPlayerScore,
            binding.etPlayerRanking,
            binding.etPlayerWeight,
            binding.etPlayerSport,
            binding.etPlayerHeight,
            binding.etPlayerAge,
        )

        for (field in fields) {
            field.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    field.error = null
                }
            }
        }
    }

    private fun checkFields(): Boolean {
        val fields: List<EditText> = listOf(
            binding.etPlayerName,
            binding.etPlayerCountry,
            binding.etPlayerScore,
            binding.etPlayerRanking,
            binding.etPlayerWeight,
            binding.etPlayerSport,
            binding.etPlayerHeight,
            binding.etPlayerAge,
        )

        for (field in fields) {
            if (field.text.isEmpty()) {
                field.error = "Поле не может быть пустым"
                return false
            }
        }

        return true
    }

    companion object {
        fun newInstance(olympicPlayer: OlympicPlayer) =
            OlympicPlayerInputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, Gson().toJson(olympicPlayer))
                }
            }
    }

}