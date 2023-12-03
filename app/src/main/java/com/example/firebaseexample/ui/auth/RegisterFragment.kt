package com.example.firebaseexample.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseexample.R
import com.example.firebaseexample.data.model.User
import com.example.firebaseexample.databinding.FragmentRegisterBinding
import com.example.firebaseexample.util.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.registerBtn.setOnClickListener {
            if (validation()){
                viewModel.register(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(viewLifecycleOwner) { state ->
            when(state){
                is UIState.Loading -> {
                    binding.registerBtn.text = getString(R.string.empty)
                    binding.registerProgress.show()
                }
                is UIState.Failure -> {
                    binding.registerBtn.text = getString(R.string.register)
                    binding.registerProgress.hide()
                    toast(state.error)
                }
                is UIState.Success -> {
                    binding.registerBtn.text = getString(R.string.register)
                    binding.registerProgress.hide()
                    if (state.data == getString(R.string.registration_session_fail)){
                        toast(getString(R.string.registration_session_failed))
                    }else{
                        toast(getString(R.string.registration_success))
                    }

                    findNavController().navigate(R.id.action_registerFragment_to_noteListFragment)
                }
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = getString(R.string.empty),
            firstName = binding.firstNameEt.text.toString(),
            lastname = binding.lastNameEt.text.toString(),
            jobTitle = binding.jobTitleEt.text.toString(),
            email = binding.emailEt.text.toString(),
        )
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.firstNameEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_first_name))
        }

        if (binding.lastNameEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_last_name))
        }

        if (binding.jobTitleEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_job_title))
        }

        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.emailEt.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_password))
        }else{
            if (binding.passEt.text.toString().length < 6){
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }
}