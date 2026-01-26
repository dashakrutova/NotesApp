package com.example.notesapp.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.App
import com.example.notesapp.R
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NoteViewModelFactory(private val noteId: Int) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteViewModel(noteId) as T
    }
}

class NoteViewModel(private val noteId: Int) : ViewModel() {
    val screenStateFlow: StateFlow<NoteScreenState> get() = mutableScreenState.asStateFlow()

    private val mutableScreenState = MutableStateFlow(NoteScreenState())

    private val repository = NotesRepository()

    init{
        loadNote(noteId)
    }

    private fun loadNote(id: Int) {
        val note = repository.getNote(id)
        val currentState = mutableScreenState.value

        mutableScreenState.value = currentState.copy(
            title = note.title,
            text = note.text
        )
    }

    fun onSaveNoteClick(inputTitle: String, inputText: String) {
        val noteTitle = App.getContext().getString(R.string.title_note)
        val finalTitle = inputTitle.ifBlank { "$noteTitle $noteId" }

        val note = Note(
            id = noteId,
            title = finalTitle,
            text = inputText
        )
        repository.saveNote(note)

        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(isSaveFinished = true)
    }

    fun onTitleChanged(newTitle: String) {
        if (mutableScreenState.value.title != newTitle) {
            val currentState = mutableScreenState.value
            mutableScreenState.value = currentState.copy(title = newTitle)
        }
    }

    fun onTextChanged(newText: String) {
        if (mutableScreenState.value.text != newText) {
            val currentState = mutableScreenState.value
            mutableScreenState.value = currentState.copy(text = newText)
        }
    }
}

data class NoteScreenState(
    val title: String = "",
    val text: String = "",

    // На случай если понадобиться проверка
    val isSaveFinished: Boolean = false
)