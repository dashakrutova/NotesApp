package com.example.notesapp.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.ItemNoteBinding

class NoteViewHolder(
    private val binding: ItemNoteBinding
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var note: Note

    fun bind(
        note: Note,
        onNoteClick: (Int) -> Unit,
        onNoteDelete: (Int) -> Unit
    ) {
        this.note = note

        showTitle()
        showNote()
        setListeners(onNoteClick, onNoteDelete)
    }

    private fun setListeners(
        onNoteClick: (Int) -> Unit,
        onNoteDelete: (Int) -> Unit,
    ) {
        val noteId = note.id
        binding.root.setOnClickListener { onNoteClick(noteId) }
        binding.btnDelete.setOnClickListener { onNoteDelete(noteId) }
    }

    private fun showNote() {
        val emptyText = binding.root.context.getString(R.string.empty_note_body)
        binding.tvBody.text = note.text.ifEmpty { emptyText }
    }

    private fun showTitle() {
        binding.tvTitle.text = note.title
    }

    companion object {
        fun from(parent: ViewGroup): NoteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
            return NoteViewHolder(binding)
        }
    }
}