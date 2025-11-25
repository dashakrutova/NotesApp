package com.example.notesapp

import android.content.Context
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
    private val prefs by lazy {
        requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
    }
    private var binding: FragmentNoteBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: NoteFragmentArgs by navArgs()
        val id = args.idNote

        loadNote(id)

        binding?.btnSave?.setOnClickListener {
            saveNote(id)
            findNavController().navigateUp()
        }
    }

    private fun loadNote(id: Int){
        val title = prefs.getString("note_title_$id", "Заметка $id")
        val text = prefs.getString("note_text_$id", "")

        binding?.etTitle?.setText(title)
        binding?.etText?.setText(text)
    }

    private fun saveNote(id: Int){
        val title = binding?.etTitle?.text.toString()
        val text = binding?.etText?.text.toString()

        prefs.edit()
            .putString("note_title_$id", title)
            .putString("note_text_$id", text)
            .apply()
        Toast.makeText(requireContext(), "Сохранено", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}