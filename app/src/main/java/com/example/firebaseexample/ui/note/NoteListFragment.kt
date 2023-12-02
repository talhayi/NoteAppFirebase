package com.example.firebaseexample.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.firebaseexample.R
import com.example.firebaseexample.databinding.FragmentNoteListBinding
import com.example.firebaseexample.ui.auth.AuthViewModel
import com.example.firebaseexample.util.UIState
import com.example.firebaseexample.util.hide
import com.example.firebaseexample.util.show
import com.example.firebaseexample.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    val TAG: String = "NoteListFragment"
    private lateinit var binding: FragmentNoteListBinding
    private val noteViewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val adapter by lazy {
        NoteListAdapter(
            onItemClicked = { position, item ->
                findNavController().navigate(
                    R.id.action_noteListFragment_to_noteDetailFragment,
                    Bundle().apply {
                        putParcelable("note", item)
                    })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return if (this::binding.isInitialized){
            binding.root
        }else {
            binding = FragmentNoteListBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = staggeredGridLayoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.itemAnimator = null
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment)
        }
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_noteListFragment_to_loginFragment)
            }
        }
        authViewModel.getSession {
            noteViewModel.getNotes(it)
        }
    }

    private fun observer(){
        noteViewModel.notes.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UIState.Loading -> {
                    binding.progressBar.show()
                }

                is UIState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }

                is UIState.Success -> {
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }
}