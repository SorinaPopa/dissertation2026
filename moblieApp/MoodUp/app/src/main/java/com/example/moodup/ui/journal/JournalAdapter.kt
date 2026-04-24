package com.example.moodup.ui.journal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import com.example.moodup.databinding.RecyclerViewJournalEntryBinding
import com.example.moodup.ui.journal.business.JournalEntry

class JournalAdapter(
    private val onClickCallback: (message: String, view: View) -> Unit
) :
    ListAdapter<JournalEntry, RecyclerView.ViewHolder>(DiffCallback()) {

    class JournalEntryViewHolder(private val recyclerViewHolderJournalEntryBinding: RecyclerViewJournalEntryBinding) :
        RecyclerView.ViewHolder(recyclerViewHolderJournalEntryBinding.root) {
        fun bind(journalEntry: JournalEntry) {
            recyclerViewHolderJournalEntryBinding.journalEntry.text = journalEntry.journalEntryText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return JournalEntryViewHolder(
            RecyclerViewJournalEntryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val journalEntry = getItem(position)
        (holder as JournalEntryViewHolder).bind(journalEntry)

        holder.itemView.setOnLongClickListener {
            if (holder.adapterPosition != -1) {
                onClickCallback(journalEntry.journalEntryText, holder.itemView)
            }
            true
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<JournalEntry>() {
        override fun areItemsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem.journalEntryId == newItem.journalEntryId
        }

        override fun areContentsTheSame(oldItem: JournalEntry, newItem: JournalEntry): Boolean {
            return oldItem == newItem
        }

    }
}