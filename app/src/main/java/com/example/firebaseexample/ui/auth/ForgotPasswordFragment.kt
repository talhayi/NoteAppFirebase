package com.example.firebaseexample.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.firebaseexample.R
import com.example.firebaseexample.databinding.FragmentForgotPasswordBinding
import com.example.firebaseexample.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        binding.forgotPassBtn.setOnClickListener {
            if (validation()){
                viewModel.forgotPassword(binding.emailEt.text.toString())
            }
        }
    }

    private fun observer(){
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when(state){
                is UIState.Loading -> {
                    binding.forgotPassBtn.text = getString(R.string.empty)
                    binding.forgotPassProgress.show()
                }
                is UIState.Failure -> {
                    binding.forgotPassBtn.text = getString(R.string.send)
                    binding.forgotPassProgress.hide()
                    toast(state.error)
                }
                is UIState.Success -> {
                    binding.forgotPassBtn.text = getString(R.string.send)
                    binding.forgotPassProgress.hide()
                    if (state.data == getString(R.string.forgot_password_success)){
                        toast(getString(R.string.forgot_password_successful))
                    }else{
                        toast(getString(R.string.forgot_password_failed))
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_email))
        }else{
            if (!binding.emailEt.text.toString().isValidEmail()){
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }

        return isValid
    }


}