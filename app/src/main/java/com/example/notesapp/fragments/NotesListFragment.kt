package com.example.notesapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapters.NotesAdapter
import com.example.notesapp.data.NoteListItem
import com.example.notesapp.data.NotesRepository
import com.example.notesapp.databinding.FragmentNotesListBinding

class NotesListFragment : Fragment() {
    private val repository by lazy{
        NotesRepository(requireContext())
    }
    private val binding get() = _binding!!
    private var _binding: FragmentNotesListBinding? = null
    private val notesAdapter get() = _notesAdapter!!
    private var _notesAdapter: NotesAdapter? = null

    private var isGridLayout = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _notesAdapter = NotesAdapter(
            onNoteClick = { noteId -> openNoteEditor(noteId) },
            onNoteDelete = { noteId -> deleteNote(noteId) }
        )

        binding.toolbar.title = "Заметки"

        binding.toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_notes_list, menu)

                updateMenuIcon(menu.findItem(R.id.action_switch_layout))
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_switch_layout -> {
                        isGridLayout = !isGridLayout
                        setupLayoutManager()
                        updateMenuIcon(menuItem)
                        loadNotes()
                        true
                    }

                    R.id.action_clear_all -> {
                        clearAllNotes()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        setupLayoutManager()
        binding.recyclerView.adapter = notesAdapter

        binding.fabAdd.setOnClickListener {
            val newId = repository.getNextId()
            openNoteEditor(newId)
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _notesAdapter = null
        _binding = null
    }

    private fun setupLayoutManager() {
        val layoutManager = GridLayoutManager(requireContext(), 2)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (isGridLayout) {
                    if (notesAdapter.getItemViewType(position) == 0) 2 else 1
                } else {
                    2
                }
            }
        }
        binding.recyclerView.layoutManager = layoutManager
    }

    private fun updateMenuIcon(menuItem: MenuItem) {
        val iconRes = if (isGridLayout) {
            R.drawable.ic_view_list
        } else {
            R.drawable.ic_view_grid
        }
        menuItem.icon = ContextCompat.getDrawable(requireContext(), iconRes)
    }
    private fun openNoteEditor(noteId: Int){
        val action = NotesListFragmentDirections.Companion.actionNotesListFragmentToNoteFragment(noteId)
        findNavController().navigate(action)
    }

    private fun loadNotes() {
        val notes = repository.getNotes()
        val isEmpty = notes.isEmpty()

        binding.tvEmptyState.isVisible = isEmpty
        binding.recyclerView.isVisible = !isEmpty

        val items = if (isEmpty) {
            emptyList()
        }
        else {
            val headerTitle = if (isGridLayout) "Режим: Сетка" else "Режим: Список"
            buildList {
                add(NoteListItem.Header(headerTitle))
                addAll(notes.map{ NoteListItem.NoteItem(it)})
            }
        }
        notesAdapter.updateData(items)
    }
    private fun deleteNote(noteId: Int){
        repository.deleteNote(noteId)
        loadNotes()
    }
    private fun clearAllNotes(){
        repository.clearAllNotes()
        loadNotes()
    }
}