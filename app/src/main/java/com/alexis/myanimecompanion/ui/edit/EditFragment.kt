package com.alexis.myanimecompanion.ui.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.alexis.myanimecompanion.R
import com.alexis.myanimecompanion.data.AnimeRepository
import com.alexis.myanimecompanion.databinding.FragmentEditBinding
import com.alexis.myanimecompanion.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

private const val MAX_RATING = 10
private const val TAG = "EditFragment"
const val EDIT_EVENT = "edit_event"

class EditFragment : BottomSheetDialogFragment() {
    lateinit var viewModel: EditViewModel
    @Inject lateinit var animeRepository: AnimeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (requireContext() as MainActivity).appComponent.inject(this)

        val binding = FragmentEditBinding.inflate(inflater)
        val args: EditFragmentArgs by navArgs()
        val viewModelFactory = EditViewModelFactory(args.anime, animeRepository)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[EditViewModel::class.java]

        viewModel.editEvent.observe(viewLifecycleOwner) { editEvent ->
            if (editEvent != null) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(EDIT_EVENT, editEvent)
                viewModel.onDialogDismiss()
                dialog?.dismiss()
            }
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                viewModel.doneShowingToast()
            }
        }
        viewModel.deleteClickEvent.observe(viewLifecycleOwner) { deleteClickEvent ->
            if (deleteClickEvent) showDeleteConfirmationDialog()
        }

        binding.apply {
            val viewModel = this@EditFragment.viewModel
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
                viewModel.applyChanges()
            }
            btnEditDialogClose.setOnClickListener {
                dialog?.cancel()
            }
            btnEditDialogDelete.setOnClickListener {
                viewModel.onDeleteClick()
            }
        }
        return binding.root
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.confirm_text)) { _, _ ->
                viewModel.onConfirmDelete()
            }
            .setNegativeButton(getString(R.string.cancel_text)) { _, _ ->
                viewModel.onCancelDelete()
            }
            .create()
            .show()
    }
}
