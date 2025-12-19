package com.example.notesapp

import NotesAdapter
import NotesRepository
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesapp.databinding.FragmentNotesListBinding

class NotesListFragment : Fragment() {

    private val repository by lazy{
        NotesRepository(requireContext())
    }
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val notesAdapter = NotesAdapter { noteId ->
        openNoteEditor(noteId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.header.let { header ->
            header.tvHeaderTitle.text = "Заметки"
            header.btnBack.visibility = View.GONE
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = notesAdapter // Подключаем наш адаптер
        }

        binding.btnClearAll.setOnClickListener {
            clearAllNotes()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun openNoteEditor(noteId: Int){
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteFragment(noteId)
        findNavController().navigate(action)
    }

    private fun loadNotes() {
        val notes = repository.getNotes()
        notesAdapter.updateData(notes)
    }
    private fun clearAllNotes(){
        repository.clearAllNotes()
        loadNotes()
    }
}