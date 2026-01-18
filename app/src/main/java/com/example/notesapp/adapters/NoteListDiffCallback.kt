package com.example.notesapp.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.notesapp.data.NoteListItem

class NoteListDiffCallback(
    val oldList: List<NoteListItem>,
    val newList: List<NoteListItem>
): DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return when{
            oldItem is NoteListItem.Header && newItem is NoteListItem.Header -> true

            oldItem is NoteListItem.NoteItem && newItem is NoteListItem.NoteItem ->
                oldItem.note.id == newItem.note.id

            else -> false
        }
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}