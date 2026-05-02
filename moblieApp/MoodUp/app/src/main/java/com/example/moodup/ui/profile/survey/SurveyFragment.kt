package com.example.moodup.ui.profile.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.moodup.R
import com.example.moodup.databinding.FragmentSurveyBinding

class SurveyFragment : Fragment() {

    private val surveyViewModel: SurveyViewModel by viewModels()
    private lateinit var binding: FragmentSurveyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSurveyBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.surveyViewModel = surveyViewModel

        arrowButtonListener()

        return binding.root

    }

    private fun arrowButtonListener() {
        surveyViewModel.onArrowButtonClicked.observe(viewLifecycleOwner) { isClicked ->
            if (isClicked) {
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                surveyViewModel.onArrowButtonClicked.value = false
            }
        }
    }

}