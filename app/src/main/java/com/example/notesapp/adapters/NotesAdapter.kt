package com.example.notesapp.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.NoteListDiffCallback
import com.example.notesapp.data.NoteListItem
import com.example.notesapp.adapters.viewholders.HeaderViewHolder
import com.example.notesapp.adapters.viewholders.NoteViewHolder

class NotesAdapter(
    private val onNoteClick: (noteId: Int) -> Unit,
    private val onNoteDelete: (noteId: Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = listOf<NoteListItem>()

    companion object{
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTE = 1
    }

    fun updateData(newList: List<NoteListItem>) {

        val diffCallback = NoteListDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)

        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is NoteListItem.Header -> TYPE_HEADER
            is NoteListItem.NoteItem -> TYPE_NOTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.Companion.from(parent)
            else -> NoteViewHolder.Companion.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(holder){
            is HeaderViewHolder -> holder.bind(item as NoteListItem.Header)
            is NoteViewHolder -> holder.bind((item as NoteListItem.NoteItem).note, onNoteClick, onNoteDelete)
        }
    }

    override fun getItemCount(): Int = items.size
}