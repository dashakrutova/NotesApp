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
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.databinding.FragmentNoteBinding
import kotlinx.coroutines.launch

// TODO: Добавить ViewModel
class NoteFragment : Fragment() {

    private val viewModel by viewModels<NoteViewModel> {
        NoteViewModelFactory()
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

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

        val args: NoteFragmentArgs by navArgs()
        val id = args.idNote
        viewModel.loadNote(id)

        binding.btnSave.setOnClickListener {
            saveNote(id)
            findNavController().navigateUp()
        }

        setupToolbar()
        observeScreenState()
        setupInsets()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        val note = state.note
        binding.etTitle.setText(note?.title)
        binding.etText.setText(note?.text)
    }

    private fun saveNote(id: Int){

        val inputTitle = binding.etTitle.text.toString()
        val inputText = binding.etText.text.toString()

        val noteTitle = getString(R.string.title_note)
        val finalTitle = inputTitle.ifBlank { "$noteTitle $id" }

        val note = Note(
            id = id,
            title = finalTitle,
            text = inputText
        )

        viewModel.onSaveNote(note)
        Toast.makeText(requireContext(), getString(R.string.message_save), Toast.LENGTH_SHORT).show()
    }
}