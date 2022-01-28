package com.alexis.myanimecompanion.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val MAX_RATING = 10
private const val TAG = "EditFragment"
const val EDIT_EVENT = "edit_event"

class EditFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentEditBinding.inflate(inflater)
        val args: EditFragmentArgs by navArgs()
        val animeRepository = AnimeRepository.getInstance(requireNotNull(context))
        val viewModelFactory = EditViewModelFactory(args.anime, animeRepository)
        val viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[EditViewModel::class.java]

        viewModel.editEvent.observe(viewLifecycleOwner) { editEvent ->
            if (editEvent != null) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(EDIT_EVENT, editEvent)
                viewModel.onDialogDismiss()
                dialog?.dismiss()
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            this.viewModel = viewModel

            spnEditDialogStatus.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                resources.getStringArray(R.array.anime_status)
            )
            spnEditDialogEpWatched.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                IntRange(0, viewModel.anime?.details?.numEpisodes ?: 0).toList()
            )
            spnEditDialogRating.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                IntRange(0, MAX_RATING).toList()
            )
            btnEditDialogSave.setOnClickListener {
                viewModel.updateAnime()
            }
            btnEditDialogClose.setOnClickListener {
                dialog?.cancel()
            }
            btnEditDialogDelete.setOnClickListener {
                viewModel.deleteAnime()
            }
        }
        return binding.root
    }
}
