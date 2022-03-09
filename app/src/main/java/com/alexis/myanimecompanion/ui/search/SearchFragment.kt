package com.alexis.myanimecompanion.ui.search

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentSearchBinding
import com.alexis.myanimecompanion.dismissKeyboard
import com.alexis.myanimecompanion.domain.Anime
import com.alexis.myanimecompanion.ui.MainActivity
import javax.inject.Inject

class SearchFragment : Fragment(), SearchListAdapter.ClickListener {
    private lateinit var viewModel: SearchViewModel
    @Inject lateinit var animeRepository: AnimeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireContext() as MainActivity).appComponent.inject(this)

        val binding = FragmentSearchBinding.inflate(inflater)

        val viewModelFactory = SearchViewModelFactory(animeRepository, resources)
        val adapter = SearchListAdapter(this)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[SearchViewModel::class.java]
        viewModel.resultList.observe(viewLifecycleOwner, { animeList ->
            animeList?.let {
                adapter.submitList(animeList)
            }
        })
        viewModel.toastMessage.observe(viewLifecycleOwner, { message ->
            message?.let {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.doneShowingToast()
            }
        })

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.rvSearchResultList.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addOnScrollListener(createOnScrollListener())
        }
        binding.etSearchQuery.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    viewModel.onSearchClick()
                    activity?.dismissKeyboard()
                    return true
                }
                return false
            }
        })

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

    private fun createOnScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                recyclerView.adapter?.let { adapter ->
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager
                    val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (lastVisibleItemPosition == adapter.itemCount - 1) {
                        viewModel.loadMore(lastVisibleItemPosition)
                    }
                }
            }
        }
    }
}
