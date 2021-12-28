package com.alexis.myanimecompanion.ui.useranime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.databinding.FragmentUserAnimeBinding

class UserAnimeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentUserAnimeBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_user_anime,
            container,
            false
        )

        val viewModelFactory = UserAnimeViewModelFactory()
        val adapter = AnimeListAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.userAnimeList.adapter = adapter

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
