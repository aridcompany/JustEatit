package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.view.View
import android.widget.RatingBar
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_feedback.*


@AndroidEntryPoint
class FeedBackFragment : Fragment(R.layout.fragment_feedback) {

    var _rating: String = "1"
    val viewModel: ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        btn_save_feedback.setOnClickListener {
            viewModel.createFeedback(_rating, etComment.text.toString())
        }

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                FeedBackFragmentDirections.actionFeedBackFragmentToMainProfileFragment()
            )
        }

        ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, rating, fromUser ->
                _rating = rating.toString()
                if (rating.toInt() == 1)
                    rating_compliment.text = getString(R.string.title_poor)
                else if (rating.toInt() == 2)
                    rating_compliment.text = getString(R.string.title_bad)
                else if (rating.toInt() == 3)
                    rating_compliment.text = getString(R.string.title_fair)
                else if (rating.toInt() == 4)
                    rating_compliment.text = getString(R.string.title_good)
                else if (rating.toInt() == 5)
                    rating_compliment.text = getString(R.string.title_excellent)
                else if (rating.toInt() == 0)
                    btn_save_feedback.isEnabled = false
            }
    }

    private fun subscribeToObservers() {
        viewModel.feedbackStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackbar(it) },
            onLoading = { snackbar(getString(R.string.title_submitting_rating)) }
        ){
            Toast.makeText(
                requireContext(),
                it,
                Toast.LENGTH_SHORT
            ).show()
        })
    }
}