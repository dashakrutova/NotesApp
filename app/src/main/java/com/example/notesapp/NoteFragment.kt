package com.example.notesapp

import Note
import NotesRepository
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.databinding.FragmentNoteBinding

class NoteFragment : Fragment() {

    private val repository by lazy{
        NotesRepository(requireContext())
    }
    private var _binding: FragmentNoteBinding? = null
    private val binding get()= _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.header.let { headerBinding ->
            headerBinding.tvHeaderTitle.text = "Заметки"
            headerBinding.btnBack.visibility = View.VISIBLE
        }

        val args: NoteFragmentArgs by navArgs()
        val id = args.idNote

        loadNote(id)

        binding.btnSave.setOnClickListener {
            saveNote(id)
            findNavController().navigateUp()
        }
        binding.header.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNote(id: Int){
        val note = repository.getNote(id)
        binding.etTitle.setText(note.title)
        binding.etText.setText(note.text)
    }

    private fun saveNote(id: Int){
        val note = Note(
            id = id,
            title = binding.etTitle.text.toString(),
            text = binding.etText.text.toString()
        )
        repository.saveNote(note)
        Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
    }
}