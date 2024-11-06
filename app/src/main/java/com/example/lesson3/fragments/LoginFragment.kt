package com.example.lesson3.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.lesson3.R
import com.example.lesson3.data.UserLogin
import com.example.lesson3.databinding.FragmentLoginBinding
import com.example.lesson3.interfaces.MainActivityCallbacks
import com.example.lesson3.repository.AppRepository

class LoginFragment : Fragment() {

    companion object {
        private var INSTANCE: LoginFragment ?= null

        fun getInstance(): LoginFragment {
            if (INSTANCE == null) INSTANCE = LoginFragment()
            return INSTANCE ?: throw Exception("LoginFragment not created")
        }

        fun newInstance(): LoginFragment {
            INSTANCE = LoginFragment()
            return INSTANCE!!
        }
    }

    private lateinit var viewModel: AuthViewModel
    private lateinit var _binding: FragmentLoginBinding
    private var user: UserLogin = UserLogin()
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = AuthViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.etLogin.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etLogin.error = null
            }
        }

        binding.etPassword.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                binding.etPassword.error = null
            }
        }

        binding.btLogin.setOnClickListener {
            user.login = binding.etLogin.text.toString()
            user.password = binding.etPassword.text.toString()

            if (user.login.isEmpty()) {
                binding.etLogin.error = "Поле не может быть пустым"
            }

            if (user.password.isEmpty()) {
                binding.etPassword.error = "Поле не может быть пустым"
            }

            if (user.login.isNotEmpty() && user.password.isNotEmpty()) {
                viewModel.login(user)
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

        val ma = (requireActivity() as MainActivityCallbacks)
        ma.newTitle("АВТОРИЗАЦИЯ")
    }
}