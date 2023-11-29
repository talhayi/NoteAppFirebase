package com.example.firebaseexample.note

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.firebaseexample.R
import com.example.firebaseexample.databinding.FragmentNoteListBinding
import com.example.firebaseexample.util.UIState
import com.example.firebaseexample.util.hide
import com.example.firebaseexample.util.show
import com.example.firebaseexample.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    val TAG: String = "NoteListFragment"
    private lateinit var binding: FragmentNoteListBinding
    private val viewModel: NoteViewModel by viewModels()
    val adapter by lazy {
        NoteListAdapter(
            onItemClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment,Bundle().apply {
                    putString("type","view")
                    putParcelable("note",item)
                })
            },
            onEditClicked = { pos, item ->
                findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment,Bundle().apply {
                    putString("type","edit")
                    putParcelable("note",item)
                })
            },
            onDeleteClicked = { pos, item ->

            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_noteListFragment_to_noteDetailFragment,Bundle().apply {
                putString("type","create")
            })
        }
        viewModel.getNotes()
        viewModel.notes.observe(viewLifecycleOwner){ state->
            when(state){
                is UIState.Loading->{
                    binding.progressBar.show()
                }
                is UIState.Failure->{
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UIState.Success->{
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
            }
        }
    }
}