import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemHeaderBinding
import com.example.notesapp.databinding.ItemNoteBinding

class NotesAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = listOf<NoteListItem>()

    companion object{
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTE = 1
    }

    fun updateData(newNotes: List<Note>, isGrid: Boolean) {
        val newList = mutableListOf<NoteListItem>()

        val headerTitle = if (isGrid) "Режим: Сетка" else "Режим: Список"
        newList.add(NoteListItem.Header(headerTitle))

        newList.addAll(newNotes.map{NoteListItem.NoteItem(it)})
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
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            else -> {
                val binding = ItemNoteBinding.inflate(inflater, parent, false)
                NoteViewHolder(binding)
            }
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when(holder){
            is HeaderViewHolder -> holder.bind(item as NoteListItem.Header)
            is NoteViewHolder -> holder.bind((item as NoteListItem.NoteItem).note)
        }
    }
    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(private val binding: ItemHeaderBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(header: NoteListItem.Header){
            binding.tvHeaderTitle.text = header.title
        }
    }
    inner class NoteViewHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.tvTitle.text = note.title

            binding.tvBody.text = if (note.text.isEmpty()) "Пусто" else note.text

            binding.root.setOnClickListener {
                onClick(note.id)
            }
        }
    }
}