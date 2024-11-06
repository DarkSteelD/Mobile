package com.example.lesson3.fragments

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson3.NamesOfFragment
import com.example.lesson3.R
import com.example.lesson3.data.CompetitionType
import com.example.lesson3.data.OlympicPlayer
import com.example.lesson3.databinding.OlympicPlayerFragmentBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.myConsts
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OlympicPlayerFragment : Fragment(){

    companion object {
        private lateinit var competitionType: CompetitionType
        fun newInstance(competitionType: CompetitionType): OlympicPlayerFragment{
            this.competitionType = competitionType
            return OlympicPlayerFragment()
        }
    }

    private lateinit var adapter: OlympicPlayerAdapter
    private lateinit var filteredPlayersList: MutableLiveData<List<OlympicPlayer>>
    private lateinit var viewModel: OlympicPlayerViewModel
    private lateinit var _binding : OlympicPlayerFragmentBinding

    val binding
        get()= _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= OlympicPlayerFragmentBinding.inflate(inflater, container, false)
        binding.rvPlayers.layoutManager=
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        filteredPlayersList = MutableLiveData<List<OlympicPlayer>>().apply {
            this.value = listOf()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= ViewModelProvider(requireActivity()).get(OlympicPlayerViewModel::class.java)
        viewModel.set_CompetitionType(competitionType)

        viewModel.olympicPlayerList.observeForever {
            filteredPlayersList.value = it
        }

        adapter = OlympicPlayerAdapter(filteredPlayersList.value!!)
        binding.rvPlayers.adapter = adapter

        filteredPlayersList.observeForever {
            adapter = OlympicPlayerAdapter(it)
            binding.rvPlayers.adapter = adapter
        }

        binding.fabNewPlayer.setOnClickListener{
            editOlympicPlayer(OlympicPlayer().apply { competitionId = viewModel.competitionType!!.id })
        }

        val searchView: SearchView = binding.svPlayers

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    Log.d(myConsts.TAG, "SUBMIT QUERY: $query")
                    filter(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText == "") {
                    filteredPlayersList.postValue(viewModel.olympicPlayerList.value)
                }
                return false
            }
        })
    }

    private fun filter(text: String) {
        val filteredList: ArrayList<OlympicPlayer> = ArrayList()

        for (item in viewModel.olympicPlayerList.value!!) {
            val itemString = item.toString()

            if (itemString.contains(text)) {
                filteredList.add(item)
            }
        }

        if (filteredList.isEmpty()) {
            Toast.makeText(context, "Совпадений не найдено", Toast.LENGTH_SHORT).show()
        }
        adapter.filterList(filteredList)
    }

    private fun deleteDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление!")
            .setMessage("Вы действительно хотите удалить спортсмена ${
                viewModel.olympicPlayer?.shortContent ?: ""
            }?")
            .setPositiveButton("да"){_,_ ->
                viewModel.deletePlayers()
            }
            .setNegativeButton("нет", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editOlympicPlayer(olympicPlayer: OlympicPlayer? = null){
        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.OLYMPIC_PLAYER, olympicPlayer)
        (requireActivity() as MainActivityCallbacks).newTitle("СОСТЯЗАНИЕ ${
            viewModel.competitionType!!.name
        }")
    }

    private inner class OlympicPlayerAdapter(private val items: List<OlympicPlayer>)
        : RecyclerView.Adapter<OlympicPlayerAdapter.ItemHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OlympicPlayerAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_tram_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int= items.size

        override fun onBindViewHolder(holder: OlympicPlayerAdapter.ItemHolder, position: Int) {
            holder.bind(filteredPlayersList.value!![position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View){
            lastView?.findViewById<ConstraintLayout>(R.id.clStudent)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white))
            view.findViewById<ConstraintLayout>(R.id.clStudent).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.mygray))
            lastView= view
        }

        fun filterList(filterList: List<OlympicPlayer>) {
            filteredPlayersList.value = filterList
        }

        private inner class ItemHolder(view: View)
            : RecyclerView.ViewHolder(view) {

                private lateinit var olympicPlayer: OlympicPlayer

                fun bind(olympicPlayer: OlympicPlayer){
                    this.olympicPlayer = olympicPlayer

                    if (olympicPlayer.competitionId == viewModel.olympicPlayer?.competitionId)
                        updateCurrentView(itemView)

                    val tv = itemView.findViewById<TextView>(R.id.tvStudentName)

                    tv.text = olympicPlayer.shortContent
                    val cl = itemView.findViewById<ConstraintLayout>(R.id.clStudent)
                    cl.setOnClickListener {
                        viewModel.setCurrentPlayer(olympicPlayer)
                        updateCurrentView(itemView)
                    }
                    itemView.findViewById<ImageButton>(R.id.ibEditStudent).setOnClickListener{
                        editOlympicPlayer(olympicPlayer)
                    }
                    itemView.findViewById<ImageButton>(R.id.ibDeleteStudent).setOnClickListener{
                        deleteDialog()
                    }

                    val llb = itemView.findViewById<LinearLayout>(R.id.llStudentButtons)
                    llb.visibility=View.INVISIBLE
                    llb?.layoutParams=llb?.layoutParams.apply { this?.width=1 }
                    cl.setOnLongClickListener{
                        cl.callOnClick()
                        llb.visibility=View.VISIBLE
                        MainScope().
                        launch{
                            val lp= llb?.layoutParams
                            lp?.width= 1
                            while(lp?.width!!<350){
                                lp?.width=lp?.width!!+35
                                llb?.layoutParams=lp
                                delay(50)
                            }
                        }
                        true
                    }
                }
        }

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivityCallbacks).newTitle("Список спортсменов")
    }

}