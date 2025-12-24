package com.example.notesapp

import NotesAdapter
import NotesRepository
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.FragmentNotesListBinding

class NotesListFragment : Fragment() {

    private var isGridLayout = true
    private val repository by lazy{
        NotesRepository(requireContext())
    }
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val notesAdapter = NotesAdapter { noteId ->
        openNoteEditor(noteId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = "Заметки"

        binding.toolbar.addMenuProvider(object : androidx.core.view.MenuProvider {
            override fun onCreateMenu(menu: android.view.Menu, menuInflater: android.view.MenuInflater) {
                menuInflater.inflate(R.menu.menu_notes_list, menu)

                updateMenuIcon(menu.findItem(R.id.action_switch_layout))
            }

            override fun onMenuItemSelected(menuItem: android.view.MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_switch_layout -> {
                        isGridLayout = !isGridLayout
                        setupLayoutManager()
                        updateMenuIcon(menuItem)
                        true
                    }

                    R.id.action_clear_all -> {
                        clearAllNotes()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, androidx.lifecycle.Lifecycle.State.RESUMED)

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
        _binding = null
    }

    private fun setupLayoutManager() {
        binding.recyclerView.layoutManager = if (isGridLayout) {
            GridLayoutManager(requireContext(), 2)
        } else {
            LinearLayoutManager(requireContext())
        }
    }
    private fun updateMenuIcon(menuItem: android.view.MenuItem) {
        val iconRes = if (isGridLayout) {
            R.drawable.ic_view_list
        } else {
            R.drawable.ic_view_grid
        }
        menuItem.icon = ContextCompat.getDrawable(requireContext(), iconRes)
    }
    private fun openNoteEditor(noteId: Int){
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteFragment(noteId)
        findNavController().navigate(action)
    }

    private fun loadNotes() {
        val notes = repository.getNotes()

        if (notes.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }

        notesAdapter.updateData(notes)
    }
    private fun clearAllNotes(){
        repository.clearAllNotes()
        loadNotes()
    }
}