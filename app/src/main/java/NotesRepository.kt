import android.content.Context
import androidx.core.content.edit

class NotesRepository(context: Context) {
    private val prefs =
        context.getSharedPreferences("note_prefs", Context.MODE_PRIVATE)

    private val notesCount = 4

    fun getNotes() :List<Note> {
        val notes = mutableListOf<Note>()

        for (i in 1..notesCount) {
            val title = prefs.getString("note_title_$i", "Заметка $i") ?: "Заметка $i"
            val text = prefs.getString("note_text_$i", "") ?: ""

            notes.add(Note(i, title, text))
        }
        return notes
    }

    fun getNote(id: Int) : Note{
        val title = prefs.getString("note_title_$id", "Заметка $id") ?: "Заметка $id"
        val text = prefs.getString("note_text_$id", "") ?: ""
        return Note(id, title, text)
    }

    fun saveNote(note: Note){
        prefs.edit {
            putString("note_title_${note.id}", note.title)
            putString("note_text_${note.id}", note.text)
        }
    }

    fun clearAllNotes(){
        prefs.edit {
            for (i in 1..notesCount) {
                putString("note_title_$i", "Заметка $i")
                putString("note_text_$i", "")
            }
        }
    }
}