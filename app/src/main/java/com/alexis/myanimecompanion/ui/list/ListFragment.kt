package com.alexis.myanimecompanion.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentListBinding
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.ui.edit.EDIT_EVENT
import com.alexis.myanimecompanion.ui.edit.EditEvent

class ListFragment : Fragment(), ListAdapter.ClickListener {
    private lateinit var viewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentListBinding.inflate(inflater)
        val animeRepository: AnimeRepository = AnimeRepository.getInstance(requireNotNull(context))
        val viewModelFactory = ListViewModelFactory(animeRepository, resources)
        val adapter = ListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ListViewModel::class.java]
        viewModel.animeList.observe(viewLifecycleOwner) { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvList.apply {
            this.adapter = adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBackStackEntry = findNavController().getBackStackEntry(R.id.ListFragment)
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

    private fun navigateToDetailsFragment(anime: Anime) {
        val directions = ListFragmentDirections
            .actionMyListFragmentToDetailsFragment(anime)
        findNavController().navigate(directions)
    }

    private fun navigateToEditFragment(anime: Anime) {
        val directions = ListFragmentDirections
            .actionMyListFragmentToEditFragment(anime)
        findNavController().navigate(directions)
    }

    override fun onItemClick(anime: Anime) {
        navigateToDetailsFragment(anime)
    }

    override fun onEditClick(anime: Anime, view: View) {
        navigateToEditFragment(anime)
    }

    override fun onIncrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit) {
        viewModel.editWatchedEpisodes(anime, ListViewModel.WatchedEpisodesEditType.INCREMENT)
        notifyDatasetChanged()
    }

    override fun onDecrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit) {
        viewModel.editWatchedEpisodes(anime, ListViewModel.WatchedEpisodesEditType.DECREMENT)
        notifyDatasetChanged()
    }
}
