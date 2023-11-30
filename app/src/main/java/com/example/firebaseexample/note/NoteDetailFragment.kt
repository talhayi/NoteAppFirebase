package com.example.firebaseexample.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.firebaseexample.R
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.databinding.FragmentNoteDetailBinding
import com.example.firebaseexample.util.UIState
import com.example.firebaseexample.util.hide
import com.example.firebaseexample.util.show
import com.example.firebaseexample.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    val TAG: String = "NoteDetailFragment"
    private lateinit var binding: FragmentNoteDetailBinding
    private val viewModel: NoteViewModel by viewModels()
    private var isEdit = false
    private var objectNote: Note? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNoteDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        binding.button.setOnClickListener {
            if (isEdit){
                updateNote()
            }else{
                createNote()
            }
        }
    }
    private fun createNote(){
        if (validation()){
            viewModel.addNote(
                Note(
                    id = getString(R.string.empty),
                    text = binding.noteMsg.text.toString(),
                    date = Date(),
                )
            )
        }
        viewModel.addNote.observe(viewLifecycleOwner){ state->
            when(state){
                is UIState.Loading->{
                    binding.progressBar.show()
                    binding.button.text  = getString(R.string.empty)
                }
                is UIState.Failure->{
                    binding.progressBar.hide()
                    binding.button.text = getString(R.string.create)
                    toast(state.error)
                }
                is UIState.Success->{
                    binding.button.text = getString(R.string.create)
                    binding.progressBar.hide()
                    toast(getString(R.string.note_has_been_created_successfully))
                    findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteListFragment())
                }
            }
        }
    }

    private fun updateNote(){
        if (validation()){
            viewModel.updateNote(
                Note(
                    id = objectNote?.id ?: getString(R.string.empty),
                    text = binding.noteMsg.text.toString(),
                    date = Date()
                )
            )
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
            when(state){
                is UIState.Loading -> {
                    binding.progressBar.show()
                    binding.button.text = getString(R.string.empty)
                }
                is UIState.Failure -> {
                    binding.progressBar.hide()
                    binding.button.text = getString(R.string.update)
                    toast(state.error)
                }
                is UIState.Success -> {
                    binding.progressBar.hide()
                    binding.button.text = getString(R.string.update)
                    toast(state.data)
                    findNavController().navigate(NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteListFragment())
                }
            }
        }
    }

    private fun updateUI(){
        val type = arguments?.getString("type",null)
        type?.let {
            when(it){
                "view" -> {
                    isEdit = false
                    binding.noteMsg.isEnabled = false
                    objectNote = arguments?.getParcelable("note")
                    binding.noteMsg.setText(objectNote?.text)
                    binding.button.hide()
                }
                "create" -> {
                    isEdit = false
                    binding.button.text = getString(R.string.create)
                }
                "edit" -> {
                    isEdit = true
                    objectNote = arguments?.getParcelable("note")
                    binding.noteMsg.setText(objectNote?.text)
                    binding.button.text = getString(R.string.update)
                }
            }
        }
    }

    private fun validation(): Boolean{
        var isValid = true
        if (binding.noteMsg.text.toString().isNullOrEmpty()){
            isValid = false
            toast(getString(R.string.enter_message))
        }
        return isValid
    }
}