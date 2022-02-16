package com.alexis.myanimecompanion.ui.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentListBinding
import com.alexis.myanimecompanion.domain.Anime

private const val TAG = "ListFragment"

class ListFragment : Fragment(), ListAdapter.ClickListener {
    private lateinit var viewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentListBinding.inflate(inflater)
        val animeRepository: AnimeRepository = AnimeRepository.getInstance(requireNotNull(context))
        val viewModelFactory = ListViewModelFactory(animeRepository, resources)
        val adapter = ListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ListViewModel::class.java]
        viewModel.filteredAnimeList.observe(viewLifecycleOwner) { animeList ->
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

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Test onResume() called")
        viewModel.refreshAnimeList()
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
