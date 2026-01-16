package com.example.notesapp.data

import android.content.Context
import androidx.core.content.edit
import com.example.notesapp.R

class NotesRepository(context: Context) {
    companion object{
        private const val KEY_IDS = "note_ids"
        private const val KEY_TITLE_PREFIX = "note_title_"
        private const val KEY_TEXT_PREFIX = "note_text_"
    }
    private val prefs =
        context.getSharedPreferences("note_prefs", Context.MODE_PRIVATE)

    val noteDefaultTitle = context.getString(R.string.note_default_title)

    private fun getIds(): List<Int> {
        val idString = prefs.getString(KEY_IDS, "").orEmpty()
        if (idString.isEmpty()) return emptyList()
        return idString.split(",").map { it.toInt() }
    }

    private fun saveIds(ids: List<Int>) {
        prefs.edit { putString(KEY_IDS, ids.joinToString(",")) }
    }

    fun getNotes() :List<Note> {
        return getIds().map { id -> getNote(id) }
    }

    fun getNote(id: Int) : Note{
        val title = prefs.getString("${KEY_TITLE_PREFIX}$id", "$noteDefaultTitle $id") ?: "$noteDefaultTitle $id"
        val text = prefs.getString("${KEY_TEXT_PREFIX}$id", "").orEmpty()
        return Note(id, title, text)
    }

    fun saveNote(note: Note){
        val ids = getIds().toMutableList()
        if (!ids.contains(note.id)) {
            ids.add(note.id)
            saveIds(ids)
        }
        prefs.edit(){
            putString("${KEY_TITLE_PREFIX}${note.id}", note.title)
            putString("${KEY_TEXT_PREFIX}${note.id}", note.text)
        }
    }
    fun getNextId(): Int {
        val ids = getIds()
        return if (ids.isEmpty()) 1 else ids.maxOrNull()!! + 1
    }

    fun clearAllNotes(){
        prefs.edit { clear() }
    }
    fun deleteNote(id: Int){
        val ids = getIds().toMutableList()
        if (ids.contains(id)) {
            ids.remove(id)
            saveIds(ids)

            prefs.edit {
                remove("${KEY_TITLE_PREFIX}$id")
                remove("${KEY_TEXT_PREFIX}$id")
            }
        }
    }
}