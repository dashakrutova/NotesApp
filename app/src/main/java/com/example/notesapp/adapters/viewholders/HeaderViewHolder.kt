package com.example.notesapp.adapters.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.NoteListItem
import com.example.notesapp.databinding.ItemHeaderBinding

class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : RecyclerView.ViewHolder(binding.root){

    fun bind(header: NoteListItem.Header){
        binding.tvHeaderTitle.text = header.title
    }

    companion object {
        fun from(parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemHeaderBinding.inflate(layoutInflater, parent, false)
            return HeaderViewHolder(binding)
        }
    }
}