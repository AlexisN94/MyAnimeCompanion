package com.alexis.myanimecompanion.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.data.UserRepository
import com.alexis.myanimecompanion.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProfileBinding.inflate(inflater)
        val animeRepository = AnimeRepository.getInstance(requireContext())

        val viewModelFactory = ProfileViewModelFactory(animeRepository)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ProfileViewModel::class.java]

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModel
        }

        viewModel.evtStartLogin.observe(viewLifecycleOwner, { startLogin ->
            if (startLogin) {
                onStartLogin()
            }
        })

        return binding.root
    }

    private fun onStartLogin() {
        val authorizationUrl = viewModel.getAuthorizationUrl()
        val browse = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
        startActivity(browse)
        viewModel.onStartLoginHandled()
    }
}
