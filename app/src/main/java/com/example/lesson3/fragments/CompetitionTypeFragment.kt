package com.example.lesson3.fragments

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lesson3.MainActivity
import com.example.lesson3.R
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.databinding.CompetitionTypeFragmentBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CompetitionTypeFragment : Fragment(), MainActivity.Edit {

    companion object {
        private var INSTANCE : CompetitionTypeFragment?= null
        fun getInstance(): CompetitionTypeFragment {
            if (INSTANCE == null) INSTANCE= CompetitionTypeFragment()
            return INSTANCE ?: throw Exception("CompetitionTypeFragment не создан")
        }
        fun newInstance() : CompetitionTypeFragment{
            INSTANCE= CompetitionTypeFragment()
            return INSTANCE!!
        }
    }

    private lateinit var viewModel: CompetitionTypeViewModel
    private var tabPosition: Int = 0
    private lateinit var _binding: CompetitionTypeFragmentBinding
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CompetitionTypeFragmentBinding.inflate(
            inflater,
            container,
            false
        )
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CompetitionTypeViewModel::class.java)
        val ma= (requireActivity() as MainActivityCallbacks)
        ma.newTitle("ИГРА \"${
            AppRepository.getInstance().olympicGame.value?.name
        }\"")


        if (viewModel.checkLocalCompetitionTypes()) {
            viewModel.competitionTypeList.postValue(
                AppRepository.getInstance().gameCompetitionTypes
            )
        } else {
            AppRepository.getInstance().fetchCompetitionTypes(
                AppRepository.getInstance().olympicGame.value?.id.toString()
            )
        }

        AppRepository.getInstance().competitionType.observeForever {
            AppRepository.getInstance().previousTabCompetitionType.postValue(it)
        }

        viewModel.competitionTypeList.observeForever {
            createUI(it)
        }
    }

    private inner class TramRoutePageAdapter(
        fa: FragmentActivity,
        private val competitionTypes: List<CompetitionType>?
    ): FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {
            return (competitionTypes?.size ?: 0)
        }

        override fun createFragment(position: Int): Fragment{
            return OlympicPlayerFragment.newInstance((competitionTypes!![position]))
        }
    }

    private fun createUI(competitionTypeList: List<CompetitionType>){
        binding.tlGroup.clearOnTabSelectedListeners()
        binding.tlGroup.removeAllTabs()

        for (i in 0 until (competitionTypeList.size)){
            binding.tlGroup.addTab(binding.tlGroup.newTab().apply {
                text = competitionTypeList[i].name
            })
        }

        tabPosition=0
        if(viewModel.competitionType!=null)
            tabPosition =
                if(viewModel.getCompetitionTypePosition>=0)
                    viewModel.getCompetitionTypePosition
                else
                    0

        val adapter = TramRoutePageAdapter(requireActivity(), viewModel.competitionTypeList.value)
        binding.vpGroup.adapter=adapter
        TabLayoutMediator(binding.tlGroup, binding.vpGroup, true, true){
                tab,pos ->
            if (competitionTypeList.isNotEmpty() && pos < competitionTypeList.size)
                tab.text = competitionTypeList[pos].name
        }.attach()

        viewModel.setCurrentCompetitionType(tabPosition)
        binding.tlGroup.selectTab(binding.tlGroup.getTabAt(tabPosition), true)
        binding.tlGroup.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition=tab?.position!!
                val tabSection = competitionTypeList[tabPosition]

                if (viewModel.previousCompetitionType?.id != tabSection.id) {
                    viewModel.setCurrentCompetitionType(tabPosition)
                    viewModel.competitionTypeList.postValue(
                        AppRepository.getInstance().gameCompetitionTypes
                    )
                } else {
                    viewModel.setCurrentCompetitionType(tabPosition)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun append(){
        editTramRoute()
    }

    override fun update(){
        editTramRoute(viewModel.competitionType?.name ?: "")
    }

    override fun delete(){
        deleteDialog()
    }

    private fun deleteDialog(){
        if (viewModel.competitionType==null) return
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить состязание ${viewModel.competitionType?.name ?: ""}?")
            .setPositiveButton("ДА"){_,_ ->
                viewModel.deleteCompetitionType()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editTramRoute(tramRouteName: String=""){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog__string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString = mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(tramRouteName)
        messageText.text="Укажите наименование состязания"

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ")
            .setView(mDialogView)
            .setPositiveButton("подтверждаю"){_,_ ->
                if (inputString.text.isNotBlank()){
                    if (tramRouteName.isBlank())
                        viewModel.appendCompetitionType(inputString.text.toString())
                    else
                        viewModel.updateCompetitionType(inputString.text.toString())
                }
            }
            .setNegativeButton("отмена",null)
            .setCancelable(true)
            .create()
            .show()
    }

}