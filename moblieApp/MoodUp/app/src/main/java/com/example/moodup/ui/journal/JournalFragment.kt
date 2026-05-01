package com.example.moodup.ui.journal

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moodup.databinding.FragmentJournalBinding
import com.google.android.material.textfield.TextInputLayout

class JournalFragment : Fragment() {

    private val journalViewModel: JournalViewModel by viewModels()
    private lateinit var binding: FragmentJournalBinding
    private lateinit var userInput: TextInputLayout
    private val SPEECH_RECOGNIZER_REQUEST_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentJournalBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.journalViewModel = journalViewModel
        userInput = binding.journalEntryInput

        journalViewModel.loadSentimentAnalyser(requireContext())

        val adapter = JournalAdapter { _, _ -> }

        val recyclerView = binding.journalEntriesRecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        sendEntryButtonObserver(adapter, recyclerView)
        micEntryButtonObserver()

        return binding.root
    }

    private fun micEntryButtonObserver() {
        journalViewModel.onMicButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                speechRecognizerIntent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now!")
                startActivityForResult(speechRecognizerIntent, SPEECH_RECOGNIZER_REQUEST_CODE)
                journalViewModel.onMicButtonClicked.value = false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SPEECH_RECOGNIZER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            val matches: ArrayList<String>? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                userInput.editText?.setText(spokenText)
            }
        }
    }

    private fun sendEntryButtonObserver(adapter: JournalAdapter, recyclerView: RecyclerView) {

        journalViewModel.onSendButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {

                val text = journalViewModel.journalEntryInput.value?.trim() ?: ""

                if (text.isNotEmpty()) {
                    journalViewModel.processAndSaveEntry(text)
                    journalViewModel.journalEntryInput.value = ""
                } else {
                    Toast.makeText(context, "please write something first!", Toast.LENGTH_LONG)
                        .show()
                }
                journalViewModel.onSendButtonClicked.value = false
            }
        }

        journalViewModel.journalList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            recyclerView.smoothScrollToPosition(it.size)
        }
    }
}