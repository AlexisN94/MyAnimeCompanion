package com.alexis.myanimecompanion.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentSearchBinding
import com.alexis.myanimecompanion.domain.Anime

class SearchFragment : Fragment(), SearchListAdapter.ClickListener {
    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentSearchBinding.inflate(inflater)
        val animeRepository = AnimeRepository.getInstance(requireContext())
        val viewModelFactory = SearchViewModelFactory(animeRepository)
        val adapter = SearchListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[SearchViewModel::class.java]
        viewModel.resultList.observe(viewLifecycleOwner, { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvSearchResultList.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        binding.etSearchQuery.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    viewModel.search()
                    return true
                }
                return false
            }
        })

        return binding.root
    }

    override fun onItemClick(anime: Anime) {
        TODO("Not yet implemented")
    }

    override fun onStatusChange(anime: Anime) {
        TODO("Not yet implemented")
    }
}