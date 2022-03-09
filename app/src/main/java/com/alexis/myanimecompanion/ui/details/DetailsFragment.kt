package com.alexis.myanimecompanion.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentDetailsBinding
import com.alexis.myanimecompanion.ui.MainActivity
import com.alexis.myanimecompanion.ui.edit.EDIT_EVENT
import com.alexis.myanimecompanion.ui.edit.EditEvent
import javax.inject.Inject

class DetailsFragment : Fragment() {
    lateinit var viewModel: DetailsViewModel
    @Inject lateinit var animeRepository: AnimeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (requireContext() as MainActivity).appComponent.inject(this)

        val binding = FragmentDetailsBinding.inflate(inflater)
        val args: DetailsFragmentArgs by navArgs()
        val viewModelFactory = DetailsViewModelFactory(args.anime, animeRepository)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[DetailsViewModel::class.java]

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@DetailsFragment.viewModel
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.doneShowingErrorMessage()
            }
        }

        viewModel.evtEdit.observe(viewLifecycleOwner) {
            if (it) {
                showEditStatusDialog()
                viewModel.doneShowingEditDialog()
            }
        }

        viewModel.evtShowStatus.observe(viewLifecycleOwner) {
            binding.clStatusContents.visibility = if (it) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBackStackEntry = findNavController().getBackStackEntry(R.id.detailsFragment)
        val observer = LifecycleEventObserver { _, lifecycleEvent ->
            if (lifecycleEvent == Lifecycle.Event.ON_RESUME && navBackStackEntry.savedStateHandle.contains(EDIT_EVENT)) {
                navBackStackEntry.savedStateHandle.get<EditEvent>(EDIT_EVENT).let { editEvent ->
                    if (editEvent != null) {
                        viewModel.onAnimeEdit(editEvent)
                    }
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)

        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, lifecycleEvent ->
            if (lifecycleEvent == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun showEditStatusDialog() {
        viewModel.anime.value?.let { anime ->
            val direction = DetailsFragmentDirections.actionDetailsFragmentToEditFragment(anime)
            findNavController().navigate(direction)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.loading.value!!) {
            viewModel.fetchAnimeDetails()
        }
    }
}
