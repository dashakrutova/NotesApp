package com.example.notesapp.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.Note
import com.example.notesapp.databinding.ItemNoteBinding

class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(note: Note, onNoteClick: (Int) -> Unit, onNoteDelete: (Int) -> Unit) {
        binding.tvTitle.text = note.title

        binding.tvBody.text = note.text.ifEmpty { "Пусто" }

        binding.root.setOnClickListener { onNoteClick(note.id) }

        binding.btnDelete.setOnClickListener { onNoteDelete(note.id) }
    }
    companion object {
        fun from(parent: ViewGroup): NoteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
            return NoteViewHolder(binding)
        }
    }
}