package com.alexis.myanimecompanion.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentListBinding
import com.alexis.myanimecompanion.domain.Anime

class ListFragment : Fragment(), ListAdapter.ClickListener {
    private lateinit var viewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentListBinding.inflate(inflater)
        val animeRepository: AnimeRepository = AnimeRepository.getInstance(requireNotNull(context))

        val viewModelFactory = ListViewModelFactory(animeRepository, resources)
        val adapter = ListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[ListViewModel::class.java]
        viewModel.animeList.observe(viewLifecycleOwner, { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        })

        binding.apply {
            viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner

            rvList.apply {
                this.adapter = adapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }

        return binding.root
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

    private fun showItemMenuOptions(view: View, anime: Anime) {
        PopupMenu(context, view).apply {
            inflate(R.menu.rv_user_list_item_menu)
            setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.rv_user_list_item_menu_edit -> {
                        navigateToEditFragment(anime)
                        true
                    }
                    R.id.rv_user_list_item_menu_remove -> {
                        // TODO viewModel.remove(anime)
                        Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    override fun onItemClick(anime: Anime) {
        navigateToDetailsFragment(anime)
    }

    override fun onOptionsMenuClick(anime: Anime, view: View) {
        showItemMenuOptions(view, anime)
    }

    override fun onIncrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit) {
        viewModel.incrementWatchedEpisodes(anime)
        notifyDatasetChanged()
    }

    override fun onDecrementWatchedClick(anime: Anime, notifyDatasetChanged: () -> Unit) {
        viewModel.decrementWatchedEpisodes(anime)
        notifyDatasetChanged()
    }
}
