import android.content.Context
import androidx.core.content.edit

class NotesRepository(context: Context) {
    private val prefs =
        context.getSharedPreferences("note_prefs", Context.MODE_PRIVATE)

    private fun getIds(): List<Int> {
        val idString = prefs.getString("note_ids", "") ?: ""
        if (idString.isEmpty()) return emptyList()
        return idString.split(",").map { it.toInt() }
    }

    private fun saveIds(ids: List<Int>) {
        prefs.edit { putString("note_ids", ids.joinToString(",")) }
    }

    fun getNotes() :List<Note> {
        return getIds().map { id -> getNote(id) }
    }

    fun getNote(id: Int) : Note{
        val title = prefs.getString("note_title_$id", "Заметка $id") ?: "Заметка $id"
        val text = prefs.getString("note_text_$id", "") ?: ""
        return Note(id, title, text)
    }

    fun saveNote(note: Note){
        val ids = getIds().toMutableList()
        if (!ids.contains(note.id)) {
            ids.add(note.id)
            saveIds(ids)
        }
        prefs.edit {
            putString("note_title_${note.id}", note.title)
            putString("note_text_${note.id}", note.text)
        }
    }
    fun getNextId(): Int {
        val ids = getIds()
        return if (ids.isEmpty()) 1 else ids.maxOrNull()!! + 1
    }

    fun clearAllNotes(){
        prefs.edit { clear() }
    }
}