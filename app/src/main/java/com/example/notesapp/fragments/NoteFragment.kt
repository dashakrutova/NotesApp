package com.example.notesapp.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentNoteBinding
import kotlinx.coroutines.launch
import kotlin.toString

// TODO: Добавить ViewModel
class NoteFragment : Fragment() {

    private val args: NoteFragmentArgs by navArgs()
    private val viewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory(args.idNote)
    }

    private var _binding: FragmentNoteBinding? = null
    private val binding get()= _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInsets()
        setupToolbar()
        setupClickListeners()
        observeScreenState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.toolbar.setPadding(0, systemBars.top, 0, 0)

            binding.btnSave.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom + 15.dp
            }

            insets
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {

            val titleToolbar = getString(R.string.title_note_toolbar)
            title = titleToolbar

            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {

            viewModel.onSaveNoteClick(
                inputTitle = binding.etTitle.text.toString(),
                inputText = binding.etText.text.toString()
            )
        }
    }

    private fun observeScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenStateFlow.collect { state ->
                    updateNoteState(state)
                }
            }
        }
    }

    private fun updateNoteState(state: NoteScreenState) {

        binding.etTitle.setText(state.title)
        binding.etText.setText(state.text)

        if (state.isSaveFinished){

            Toast.makeText(requireContext(),
                getString(R.string.message_save),
                Toast.LENGTH_SHORT)
                .show()

            findNavController().navigateUp()
        }
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}