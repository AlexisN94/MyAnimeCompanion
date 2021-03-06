package com.alexis.myanimecompanion.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentDetailsBinding.inflate(inflater)
        val args: DetailsFragmentArgs by navArgs()

        val animeRepository = AnimeRepository.getInstance(requireContext())
        val viewModelFactory = DetailsViewModelFactory(args.anime, animeRepository)
        val viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[DetailsViewModel::class.java]

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = viewModel
        }

        return binding.root
    }
}
