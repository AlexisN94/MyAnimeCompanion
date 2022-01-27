package com.alexis.myanimecompanion.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentSearchBinding
import com.alexis.myanimecompanion.dismissKeyboard
import com.alexis.myanimecompanion.domain.Anime

class SearchFragment : Fragment(), SearchListAdapter.ClickListener {
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSearchBinding.inflate(inflater)
        val animeRepository = AnimeRepository.getInstance(requireContext())

        val viewModelFactory = SearchViewModelFactory(animeRepository, resources)
        val adapter = SearchListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[SearchViewModel::class.java]
        viewModel.resultList.observe(viewLifecycleOwner, { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        })

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModel
            rvSearchResultList.apply {
                this.adapter = adapter
                layoutManager = GridLayoutManager(requireContext(), 3)
            }
            etSearchQuery.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        viewModel.search()
                        activity?.dismissKeyboard()
                        return true
                    }
                    return false
                }
            })
        }

        return binding.root
    }

    private fun navigateToDetailsFragment(anime: Anime) {
        val direction = SearchFragmentDirections.actionSearchFragmentToDetailsFragment(anime)
        findNavController().navigate(direction)
    }

    override fun onItemClick(anime: Anime) {
        navigateToDetailsFragment(anime)
    }

    override fun onStatusChange(anime: Anime) {
        TODO("Not yet implemented")
    }
}
