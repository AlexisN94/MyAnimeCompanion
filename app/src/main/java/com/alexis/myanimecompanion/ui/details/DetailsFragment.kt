package com.alexis.myanimecompanion.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.databinding.FragmentMyListBinding

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentMyListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_details,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        // binding.viewModel = viewModel

        return binding.root
    }
}
