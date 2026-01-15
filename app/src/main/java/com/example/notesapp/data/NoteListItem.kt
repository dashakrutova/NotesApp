package com.example.notesapp.data

sealed class NoteListItem {
    data class Header(val title: String) : NoteListItem()
    data class NoteItem(val note: Note) : NoteListItem()
}