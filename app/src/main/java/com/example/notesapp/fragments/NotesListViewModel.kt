package com.example.notesapp.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.Note
import com.example.notesapp.data.NotesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesListViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotesListViewModel() as T
    }
}

class NotesListViewModel() : ViewModel() {

    // Неизменяемый Publisher состояния экрана для возможности подписки на него извне
    // Хотим, что бы наружу торчал только неизменяемый Publisher
    val screenStateFlow: StateFlow<NotesListScreenState> get() = mutableScreenState.asStateFlow()

    private val repository = NotesRepository()

    // Изменяемый Publisher состояния экрана
    // MutableStateFlow всегда хранит последний элемент
    private val mutableScreenState = MutableStateFlow(
        NotesListScreenState(notes = emptyList(), isNotesInGrid = true)
    )


    private val notes = mutableListOf<Note>()

    init {
        viewModelScope.launch { loadNotes() }
    }

    fun onViewResume() {
        viewModelScope.launch { loadNotes() }
    }

    fun onSwitchLayoutClick() {
        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(
            isNotesInGrid = currentState.isNotesInGrid.not()
        )
    }

    fun onNoteClick(noteId: Int) {
        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(noteIdToShow = noteId)
    }

    fun onDeleteNoteClick(noteId: Int) {
        repository.deleteNote(noteId)
        notes.removeIf { note -> note.id == noteId }

        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(notes = notes.toList())
    }

    fun onClearAllNotesClick() {
        repository.clearAllNotes()
        notes.clear()

        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(notes = notes.toList())
    }

    fun onAddNoteClick() {
        val newNoteId = repository.getNextId()
        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(noteIdToShow = newNoteId)
    }

    fun onNoteShown() {
        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(noteIdToShow = null)
    }

    // Потенциально долгая операция получения списка заметок из репозитория
    private suspend fun loadNotes() {
        this.notes.clear()
        this.notes.addAll(repository.getNotes())

        // Симуляция длительной работы
        // delay приостанавливает (суспендит) дальнейшее выполнение метода на какое-то время
//        delay(5000)



        val currentState = mutableScreenState.value
        mutableScreenState.value = currentState.copy(notes = notes.toList())
    }
}

// Состояние экрана
data class NotesListScreenState(
    val notes: List<Note>,
    val isNotesInGrid: Boolean,
    val noteIdToShow: Int? = null,
)