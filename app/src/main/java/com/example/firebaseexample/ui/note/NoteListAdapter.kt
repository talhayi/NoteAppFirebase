package com.example.firebaseexample.ui.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseexample.data.model.Note
import com.example.firebaseexample.databinding.ItemNoteLayoutBinding
import com.example.firebaseexample.util.addChip
import com.example.firebaseexample.util.hide
import java.text.SimpleDateFormat

class NoteListAdapter(
    val onItemClicked: (Int, Note) -> Unit,
) : RecyclerView.Adapter<NoteListAdapter.MyViewHolder>() {

    val simpleDateFormat = SimpleDateFormat("dd MM yyyy")
    private var list: MutableList<Note> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = ItemNoteLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.bind(item)
    }

    fun updateList(list: MutableList<Note>){
        this.list = list
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(private val binding: ItemNoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Note){
            binding.title.text = item.title
            binding.date.text = simpleDateFormat.format(item.date)
            binding.tags.apply {
                if (item.tags.isNullOrEmpty()){
                    hide()
                }else {
                    removeAllViews()
                    if (item.tags.size > 2) {
                        item.tags.subList(0, 2).forEach { tag -> addChip(tag) }
                        addChip("+${item.tags.size - 2}")
                    } else {
                        item.tags.forEach { tag -> addChip(tag) }
                    }
                }
            }
            binding.desc.apply {
                text = if (item.description!!.length > 120){
                    "${item.description.substring(0,120)}..."
                }else{
                    item.description
                }
            }
            binding.itemLayout.setOnClickListener { onItemClicked.invoke(bindingAdapterPosition,item) }
        }
    }
}