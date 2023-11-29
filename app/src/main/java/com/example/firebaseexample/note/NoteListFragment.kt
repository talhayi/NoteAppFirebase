package com.example.firebaseexample.note

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.firebaseexample.databinding.FragmentNoteListBinding
import com.example.firebaseexample.util.UIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteListFragment : Fragment() {
    val TAG: String = "NoteListFragment"
    private lateinit var binding: FragmentNoteListBinding
    private val viewModel: NoteViewModel by viewModels()
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

        viewModel.getNotes()
        viewModel.notes.observe(viewLifecycleOwner){state->
            when(state){
                is UIState.Loading->{
                    Log.e(TAG,"Loading")
                }
                is UIState.Failure->{
                    Log.e(TAG,state.error.toString())
                }
                is UIState.Success->{
                    state.data.forEach { note ->
                        Log.e(TAG,note.toString())
                    }
                }
            }

        }
    }
}