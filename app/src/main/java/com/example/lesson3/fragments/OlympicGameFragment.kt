package com.example.lesson3.fragments

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lesson3.MainActivity
import com.example.lesson3.NamesOfFragment
import com.example.lesson3.R
import com.example.lesson3.data.OlympicGame
import com.example.lesson3.databinding.OlympicGamesFragmentBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.myConsts
import com.example.lesson3.repository.AppRepository
import java.lang.Exception

class OlympicGameFragment:Fragment(), MainActivity.Edit {

    interface Callback{
        fun newTitle(_title: String)
    }

    companion object{
        private var INSTANCE : OlympicGameFragment?= null
        fun getInstance(): OlympicGameFragment{
            if (INSTANCE == null) INSTANCE= OlympicGameFragment()
            return INSTANCE ?: throw Exception("OlympicGameFragment не создан")
        }
    }

    private lateinit var viewModel: OlympicGameViewModel
    private var _binding: OlympicGamesFragmentBinding?=null
    val binding
        get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val ma = requireActivity() as MainActivityCallbacks
        ma.newTitle("ОЛИМПИЙСКИЕ ИГРЫ")
        _binding = OlympicGamesFragmentBinding.inflate(inflater,container,false)
        binding.rvFaculty.layoutManager=LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(OlympicGameViewModel::class.java)
        viewModel.olympicGameList.observe(viewLifecycleOwner){
            binding.rvFaculty.adapter=OlympicGameAdapter(it)
        }
    }

    override fun append() {
        editOlympicGame()
    }

    override fun update() {
        editOlympicGame(viewModel.olympicGame?.name ?: "")
    }

    override fun delete() {
        deleteDialog()
    }

    private fun deleteDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление")
            .setMessage("Вы действительно хотите удалить олимпийскую игру ${
                viewModel.olympicGame?.name ?: ""
            }?")
            .setPositiveButton("ДА") {_, _ ->
                viewModel.deleteGame()
            }
            .setNegativeButton("НЕТ", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private fun editOlympicGame(olympicGameName : String=""){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog__string, null)
        val messageText = mDialogView.findViewById<TextView>(R.id.tvInfo)
        val inputString = mDialogView.findViewById<EditText>(R.id.etString)
        inputString.setText(olympicGameName)
        messageText.text="Укажите название олимпийской игры"

        AlertDialog.Builder(requireContext())
            .setTitle("ИЗМЕНЕНИЕ ДАННЫХ")
            .setView(mDialogView)
            .setPositiveButton("ok"){_, _ ->
                if (inputString.text.isNotBlank()) {
                    if (olympicGameName.isBlank())
                        viewModel.appendGame(inputString.text.toString())
                    else
                        viewModel.updateGame(inputString.text.toString())
                }
            }
            .setNegativeButton("ОТМЕНА", null)
            .setCancelable(true)
            .create()
            .show()
    }

    private inner class OlympicGameAdapter(private val items: List<OlympicGame>)
        : RecyclerView.Adapter<OlympicGameAdapter.ItemHolder> () {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OlympicGameAdapter.ItemHolder {
            val view = layoutInflater.inflate(R.layout.element_olympic_game_list, parent, false)
            return ItemHolder(view)
        }

        override fun getItemCount(): Int= items.size

        override fun onBindViewHolder(holder: OlympicGameAdapter.ItemHolder, position: Int) {
            holder.bind(viewModel.olympicGameList.value!![position])
        }

        private var lastView: View? = null

        private fun updateCurrentView(view: View){
            lastView?.findViewById<ConstraintLayout>(R.id.clFaculty)?.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.white)
            )
            lastView = view
            view.findViewById<ConstraintLayout>(R.id.clFaculty).setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.mygray)
            )
        }

        private inner class ItemHolder(view: View)
            : RecyclerView.ViewHolder(view) {
                private lateinit var olympicGame: OlympicGame
                fun bind(olympicGame: OlympicGame){
                    this.olympicGame= olympicGame
                    val tv = itemView.findViewById<TextView>(R.id.tvFaculty)
                    tv.text = olympicGame.name
                    if (olympicGame == viewModel.olympicGame)
                        updateCurrentView(itemView)
                    tv.setOnClickListener{
                        viewModel.setGame(olympicGame)
                        updateCurrentView(itemView)
                    }
                    tv.setOnLongClickListener {
                        tv.callOnClick()
                        Log.d(myConsts.TAG, "STORE FRAGMENT ONCLICK STORE VALUE: ${
                            AppRepository.getInstance().olympicGame.value
                        }")

                        (requireActivity() as MainActivityCallbacks).showFragment(NamesOfFragment.OLYMPIC_COMPETITION)
                        true
                    }
                }
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as MainActivityCallbacks).newTitle("ОЛИМПИЙСКИЕ ИГРЫ")
    }
}