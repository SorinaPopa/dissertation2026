package com.example.moodup.ui.journal

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.moodup.databinding.FragmentJournalBinding
import com.google.android.material.textfield.TextInputLayout

class JournalFragment : Fragment() {

    private val journalViewModel: JournalViewModel by viewModels()
    private lateinit var binding: FragmentJournalBinding
    private lateinit var userInput: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentJournalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.journalViewModel = journalViewModel
        userInput = binding.journalEntryInput

        val adapter = JournalAdapter { _, _ -> }

        binding.journalEntriesRecyclerView.adapter = adapter
        binding.journalEntriesRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        journalViewModel.journalList.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
        }

        return binding.root

    }

}