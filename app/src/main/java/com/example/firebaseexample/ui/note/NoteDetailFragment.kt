package com.example.firebaseexample.ui.note

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
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import com.example.firebaseexample.ui.auth.AuthViewModel
import com.example.firebaseexample.util.*
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat


@AndroidEntryPoint
class NoteDetailFragment : Fragment() {
    val TAG: String = "NoteDetailFragment"
    private lateinit var binding: FragmentNoteDetailBinding
    private val viewModel: NoteViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var objectNote: Note? = null
    private var tagsList: MutableList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return if (this::binding.isInitialized){
            binding.root
        }else {
            binding = FragmentNoteDetailBinding.inflate(layoutInflater)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        observer()
    }

    private fun observer() {
        viewModel.addNote.observe(viewLifecycleOwner) { state ->
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
                    toast(state.data.second)
                    objectNote = state.data.first
                    isMakeEnableUI(false)
                    binding.edit.show()
                }
            }
        }
        viewModel.updateNote.observe(viewLifecycleOwner) { state ->
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
                    toast(state.data)
                    binding.done.hide()
                    binding.edit.show()
                    isMakeEnableUI(false)
                }
            }
        }

        viewModel.deleteNote.observe(viewLifecycleOwner) { state ->
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
                    toast(state.data)
                    findNavController().navigateUp()
                }
            }
        }
    }

    private fun updateUI() {
        val simpleDateFormat = SimpleDateFormat("dd MMM yyyy . hh:mm a")
        objectNote = arguments?.getParcelable("note")
        //binding.tags.layoutParams.height = 40.dpToPx
        objectNote?.let { note ->
            binding.title.setText(note.title)
            binding.date.text = simpleDateFormat.format(note.date)
            tagsList = note.tags
            addTags(tagsList)
            binding.description.setText(note.description)
            binding.done.hide()
            binding.edit.show()
            binding.delete.show()
            isMakeEnableUI(false)
        } ?: run {
            binding.title.setText(getString(R.string.empty))
            binding.date.text = simpleDateFormat.format(Date())
            binding.description.setText(getString(R.string.empty))
            binding.done.hide()
            binding.edit.hide()
            binding.delete.hide()
            isMakeEnableUI(true)
        }
        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.title.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.description.setOnClickListener {
            isMakeEnableUI(true)
        }
        binding.delete.setOnClickListener {
            objectNote?.let { viewModel.deleteNote(it) }
        }
        binding.addTagLl.setOnClickListener {
            showAddTagDialog()
        }
        binding.edit.setOnClickListener {
            isMakeEnableUI(true)
            binding.done.show()
            binding.edit.hide()
            binding.title.requestFocus()
        }
        binding.done.setOnClickListener {
            if (validation()) {
                if (objectNote == null) {
                    viewModel.addNote(getNote())
                } else {
                    viewModel.updateNote(getNote())
                }
            }
        }
        binding.title.doAfterTextChanged {
            binding.done.show()
            binding.edit.hide()
        }
        binding.description.doAfterTextChanged {
            binding.done.show()
            binding.edit.hide()
        }
    }

    private fun showAddTagDialog(){
        val dialog = requireContext().createDialog(R.layout.add_tag_dialog, true)
        val button = dialog.findViewById<MaterialButton>(R.id.tag_dialog_add)
        val editText = dialog.findViewById<EditText>(R.id.tag_dialog_et)
        button.setOnClickListener {
            if (editText.text.toString().isNullOrEmpty()) {
                toast(getString(R.string.error_tag_text))
            } else {
                val text = editText.text.toString()
                tagsList.add(text)
                binding.tags.apply {
                    addChip(text, true) {
                        tagsList.forEachIndexed { index, tag ->
                            if (text.equals(tag)) {
                                tagsList.removeAt(index)
                                binding.tags.removeViewAt(index)
                            }
                        }
                       /* if (tagsList.size == 0){
                            layoutParams.height = 40.dpToPx
                        }*/
                    }
                    //layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
                binding.done.show()
                binding.edit.hide()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun addTags(note: MutableList<String>) {
        if (note.size > 0) {
            binding.tags.apply {
                removeAllViews()
                note.forEachIndexed { index, tag ->
                    addChip(tag, true) {
                        if (isEnabled) {
                            note.removeAt(index)
                            this.removeViewAt(index)
                        }
                    }
                }
            }
        }
    }

    private fun isMakeEnableUI(isDisable: Boolean = false) {
        binding.title.isEnabled = isDisable
        binding.date.isEnabled = isDisable
        binding.tags.isEnabled = isDisable
        binding.addTagLl.isEnabled = isDisable
        binding.description.isEnabled = isDisable
    }

    private fun validation(): Boolean {
        var isValid = true
        if (binding.title.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_title))
        }
        if (binding.description.text.toString().isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.error_description))
        }
        return isValid
    }

    private fun getNote(): Note {
        //val tags = binding.tags.children.toList().map { (it as Chip).text.toString() }.toMutableList()
        return Note(
            id = objectNote?.id ?: getString(R.string.empty),
            title = binding.title.text.toString(),
            description = binding.description.text.toString(),
            tags = tagsList,
            date = Date()
        ).apply { authViewModel.getSession { this.userId = it?.id ?: ""}}
    }
}