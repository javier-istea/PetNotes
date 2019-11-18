package com.example.petnotes

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petnotes.model.Note
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class MainRecyclerAdapter(private var notes: List<Note>, private var context: Context) :
    RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.item_note_list, parent, false))
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position], context)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById(R.id.tv_item_note_title) as TextView
        private val noteContent = view.findViewById(R.id.tv_item_note_content) as TextView
        private val type = view.findViewById(R.id.ch_item_note_type) as TextView
        private val deleteButton = view.findViewById(R.id.mb_delete) as MaterialButton
        private val editButton = view.findViewById(R.id.mb_edit) as MaterialButton
        private val card = view.findViewById(R.id.mcv_container) as MaterialCardView

        private lateinit var listener: ItemInteractionListener
        fun bind(note: Note, context: Context) {
            title.text = note.title
            noteContent.text = note.message
            type.text = context.resources.getStringArray(R.array.note_types)[note.type]
            listener = context as ItemInteractionListener
            editButton.setOnClickListener {
                listener.onEditButtonCLick(note.id)
            }
            deleteButton.setOnClickListener {
                listener.onDeleteButtonCLick(note.id)
            }
            card.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle(note.title)
                    .setMessage(note.message)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }


}