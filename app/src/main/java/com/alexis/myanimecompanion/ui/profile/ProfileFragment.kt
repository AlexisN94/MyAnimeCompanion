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
import com.alexis.myanimecompanion.databinding.FragmentProfileBinding
import com.alexis.myanimecompanion.ui.MainActivity
import javax.inject.Inject

class ProfileFragment : Fragment() {
    lateinit var viewModel: ProfileViewModel
    @Inject lateinit var animeRepository: AnimeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireContext() as MainActivity).appComponent.inject(this)

        val binding = FragmentProfileBinding.inflate(inflater)

        val viewModelFactory = ProfileViewModelFactory(animeRepository)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ProfileViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.evtStartLogin.observe(viewLifecycleOwner) { startLogin ->
            if (startLogin) {
                onStartLogin()
            }
        }

        return binding.root
    }

    private fun onStartLogin() {
        val authorizationUrl = viewModel.getAuthorizationUrl()
        val browse = Intent(Intent.ACTION_VIEW, Uri.parse(authorizationUrl))
        startActivity(browse)
        viewModel.onStartLoginHandled()
    }
}
