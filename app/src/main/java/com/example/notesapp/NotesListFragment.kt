package com.example.notesapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.example.notesapp.databinding.FragmentNotesListBinding

class NotesListFragment : Fragment() {
    private var binding: FragmentNotesListBinding? = null

    private val prefs by lazy{
        requireContext().getSharedPreferences("note_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.card1?.setOnClickListener {
            openNoteEditor(1)
        }
        binding?.card2?.setOnClickListener {
            openNoteEditor(2)
        }
        binding?.card3?.setOnClickListener {
            openNoteEditor(3)
        }
        binding?.card4?.setOnClickListener {
            openNoteEditor(4)
        }
        binding?.btnClearAll?.setOnClickListener {
            clearAllNotes()
        }
    }

    override fun onResume() {
        super.onResume()
        loadCards()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
    private fun openNoteEditor(noteId: Int){
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteFragment(noteId)
        findNavController().navigate(action)
    }
    private fun loadCards(){
        loadCard(1, binding!!.card1Title, binding!!.card1Text)
        loadCard(2, binding!!.card2Title, binding!!.card2Text)
        loadCard(3, binding!!.card3Title, binding!!.card3Text)
        loadCard(4, binding!!.card4Title, binding!!.card4Text)
    }
    private fun loadCard(id: Int, titleView: TextView, textView: TextView){
        val title = prefs.getString("note_title_$id", "Заметка $id")
        val text = prefs.getString("note_text_$id", "")

        titleView.text = title

        textView.text = text
        textView.maxLines = 2
        textView.ellipsize = TextUtils.TruncateAt.END
    }
    private fun clearAllNotes(){
        val editor = prefs.edit()
        for(i in 1..4){
            editor.putString("note_title_$i", "Заметка $i")
            editor.putString("note_text_$i", "")
        }
        editor.apply()
        loadCards()
    }
}