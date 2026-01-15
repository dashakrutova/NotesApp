import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemHeaderBinding
import com.example.notesapp.databinding.ItemNoteBinding

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
        items = newList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]){
            is NoteListItem.Header -> TYPE_HEADER
            is NoteListItem.NoteItem -> TYPE_NOTE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder.from(parent)
            else -> NoteViewHolder.from(parent)
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

    class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root){
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

    class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note, onNoteClick: (Int) -> Unit, onNoteDelete: (Int) -> Unit) {
            binding.tvTitle.text = note.title

            binding.tvBody.text = note.text.ifEmpty { "Пусто" }

            binding.root.setOnClickListener { onNoteClick(note.id) }

            binding.btnDelete.setOnClickListener { onNoteDelete(note.id) }
        }
        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNoteBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding)
            }
        }
    }
}