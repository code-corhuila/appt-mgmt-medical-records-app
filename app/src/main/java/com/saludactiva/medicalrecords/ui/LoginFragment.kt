package com.saludactiva.medicalrecords.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.saludactiva.medicalrecords.R
import com.saludactiva.medicalrecords.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = requireNotNull(_binding)
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.signInButton.setOnClickListener {
            viewModel.signIn(
                binding.emailInput.text?.toString().orEmpty(),
                binding.passwordInput.text?.toString().orEmpty()
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.loginProgress.visibility =
                if (state is LoginState.Loading) View.VISIBLE else View.GONE
            binding.signInButton.isEnabled = state !is LoginState.Loading
            when (state) {
                LoginState.Authenticated ->
                    findNavController().navigate(R.id.action_loginFragment_to_recordsFragment)
                is LoginState.Error ->
                    Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                LoginState.Idle, LoginState.Loading -> Unit
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
