package com.alexis.myanimecompanion.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProfileBinding.inflate(inflater)
        val animeRepository = AnimeRepository.getInstance(requireContext())

        val viewModelFactory = ProfileViewModelFactory(animeRepository)
        val viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ProfileViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.evtStartLogin.observe(viewLifecycleOwner, { startLogin ->
            if (startLogin) {
                navigateToLoginFragment()
            }
        })

        return binding.root
    }

    private fun navigateToLoginFragment() {
        val direction = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        findNavController().navigate(direction)
        viewModel.onStartLoginHandled()
    }
}
