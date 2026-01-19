package com.example.notesapp.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel() as T
    }
}

class NoteViewModel : ViewModel() {

    val screenStateFlow: StateFlow<NoteScreenState> get() = mutableScreenState.asStateFlow()

    private val mutableScreenState = MutableStateFlow(NoteScreenState(note = null))

    private val repository = NotesRepository()

    fun loadNote(id: Int) {
        val note = repository.getNote(id)
        mutableScreenState.value = NoteScreenState(note = note)
    }

    fun onSaveNote(note: Note) {
        repository.saveNote(note)
    }
}

data class NoteScreenState(val note: Note?)