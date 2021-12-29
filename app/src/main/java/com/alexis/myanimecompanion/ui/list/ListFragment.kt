package com.alexis.myanimecompanion.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.databinding.FragmentListBinding
import com.alexis.myanimecompanion.domain.Anime

class ListFragment : Fragment(), AnimeClickListener, AnimeMenuClickListener {

    private lateinit var viewModel: ListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_list,
            container,
            false
        )
        val viewModelFactory = ListViewModelFactory()
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory).get(ListViewModel::class.java)
        val adapter = ListAdapter(this, this)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvList.adapter = adapter

        viewModel.animeList.observe(viewLifecycleOwner, { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        })

        return binding.root
    }

    private fun navigateToDetailsFragment(anime: Anime) {
        val directions = ListFragmentDirections
            .actionMyListFragmentToDetailsFragment(anime)
        navigate(directions)
    }

    private fun navigateToEditFragment(anime: Anime) {
        val directions = ListFragmentDirections
            .actionMyListFragmentToEditFragment(anime)
        navigate(directions)
    }

    private fun navigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }

    override fun onAnimeClick(anime: Anime) {
        navigateToDetailsFragment(anime)
    }

    override fun onAnimeMenuClick(view: View, anime: Anime) {
        // TODO should keep popup open on screen rotation, but can't reference views in ViewModel...
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.rv_user_list_item_menu)

        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem?): Boolean {
                item?.let {
                    when (item.itemId) {
                        R.id.rv_user_list_item_menu_edit -> {
                            navigateToEditFragment(anime)
                            return true
                        }
                        R.id.rv_user_list_item_menu_remove -> {
                            // TODO viewModel.remove(anime)
                            Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show()
                            return true
                        }
                        else -> return false
                    }
                }
                return false
            }
        })

        popupMenu.show()
    }
}
