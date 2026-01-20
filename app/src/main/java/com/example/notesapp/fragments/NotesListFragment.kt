package com.example.notesapp.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notesapp.R
import com.example.notesapp.adapters.NotesAdapter
import com.example.notesapp.data.NoteListItem
import com.example.notesapp.databinding.FragmentNotesListBinding
import kotlinx.coroutines.launch

class NotesListFragment : Fragment() {

    private val viewModel by viewModels<NotesListViewModel> {
        NotesListViewModelFactory()
    }

    private val binding get() = _binding!!
    private var _binding: FragmentNotesListBinding? = null

    private val notesAdapter get() = _notesAdapter!!

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    private var _notesAdapter: NotesAdapter? = null

    private var isGridLayout = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeScreenState()
        setupInsets()
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.toolbar.setPadding(0, systemBars.top, 0, 0)

            binding.recyclerView.setPadding(0, 0, 0, systemBars.bottom)

            binding.fabAdd.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom + 24.dp
            }

            insets
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _notesAdapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        _notesAdapter = NotesAdapter(
            onNoteClick = { noteId -> viewModel.onNoteClick(noteId) },
            onNoteDeleteClick = { noteId -> viewModel.onDeleteNoteClick(noteId) }
        )

        setupLayoutManager()
        binding.recyclerView.adapter = notesAdapter
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            viewModel.onAddNoteClick()
        }
    }

    private fun observeScreenState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenStateFlow.collect { state ->
                    updateNotesListState(state)

                    if (state.noteIdToShow != null) {
                        openNoteEditor(state.noteIdToShow)
                        viewModel.onNoteShown()
                    }
                }
            }
        }
    }

    private fun updateNotesListState(state: NotesListScreenState) {
        val notes = state.notes
        val isEmpty = notes.isEmpty()

        isGridLayout = state.isNotesInGrid
        binding.tvEmptyState.isVisible = isEmpty
        binding.recyclerView.isVisible = !isEmpty

        val items = if (isEmpty) {
            emptyList()
        } else {
            val modeGrid = getString(R.string.mode_grid)
            val modeList = getString(R.string.mode_list)

            val headerTitle = if (isGridLayout) modeGrid else modeList
            buildList {
                add(NoteListItem.Header(headerTitle))
                addAll(notes.map { NoteListItem.NoteItem(it) })
            }
        }
        notesAdapter.updateData(items)
    }

    private fun setupToolbar() {
        val titleToolbar = getString(R.string.title_notes_list_toolbar)
        binding.toolbar.title = titleToolbar

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
                        viewModel.onSwitchLayoutClick()
                        true
                    }

                    R.id.action_clear_all -> {
                        viewModel.onClearAllNotesClick()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupLayoutManager() {
        val layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when {
                    isGridLayout.not() -> SPAN_COUNT
                    notesAdapter.isHeaderItem(position) -> SPAN_COUNT
                    else -> SPAN_COUNT / SPAN_COUNT
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

    private fun openNoteEditor(noteId: Int) {
        val action = NotesListFragmentDirections.actionNotesListFragmentToNoteFragment(noteId)
        findNavController().navigate(action)
    }

    private companion object {
        const val SPAN_COUNT = 2
    }
}