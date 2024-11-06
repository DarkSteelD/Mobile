package com.example.lesson3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.lesson3.data.User
import com.example.lesson3.databinding.FragmentRegistrationBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository

private const val ARG_PARAM1 = "student_param"

class RegistrationFragment : Fragment() {

    companion object {
        private var INSTANCE: RegistrationFragment ?= null

        fun getInstance(): RegistrationFragment {
            if (INSTANCE == null) INSTANCE = RegistrationFragment()
            return INSTANCE ?: throw Exception("RegistrationFragment not created")
        }

        fun newInstance(): RegistrationFragment {
            INSTANCE = RegistrationFragment()
            return INSTANCE!!
        }
    }

    private var user: User  = User()
    private lateinit var viewModel: AuthViewModel
    private lateinit var _binding: FragmentRegistrationBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val param1 = it.getString(ARG_PARAM1)
        }

        viewModel = AuthViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        binding.etLoginSignup.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etLoginSignup.error = null
            }
        }

        binding.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etPassword.error = null
            }
        }

        binding.btRegister.setOnClickListener {
            user.name = binding.etName.text.toString()
            user.surname = binding.etSurname.text.toString()
            user.login = binding.etLoginSignup.text.toString()
            user.password = binding.etPassword.text.toString()

            if (user.login.isEmpty()) {
                binding.etLoginSignup.error = "Поле не может быть пустым"
            }

            if (user.password.isEmpty()) {
                binding.etPassword.error = "Поле не может быть пустым"
            }

            if (user.login.isNotEmpty() && user.password.isNotEmpty()) {
                viewModel.registration(user)
                requireActivity().onBackPressedDispatcher.onBackPressed()
            } else {
                Toast.makeText(
                    context,
                    "Заполните обязательные поля",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        val ma = (requireActivity() as MainActivityCallbacks)
        ma.newTitle("РЕГИСТРАЦИЯ \"${viewModel}\"")
    }

}