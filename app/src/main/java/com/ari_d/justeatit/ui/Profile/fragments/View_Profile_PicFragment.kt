package com.ari_d.justeatit.ui.Profile.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.databinding.FragmentViewProfilePicBinding
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_view_profile_pic.*
import kotlinx.android.synthetic.main.fragment_view_profile_pic.img_profile

@AndroidEntryPoint
class View_Profile_PicFragment : Fragment(R.layout.fragment_view_profile_pic) {

    val viewModel: ProfileViewModel by viewModels()
    private val args: View_Profile_PicFragmentArgs by navArgs()
    private var _binding: FragmentViewProfilePicBinding? = null
    private val binding: FragmentViewProfilePicBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentViewProfilePicBinding.bind(view)
        subscribeToObservers()
        if (args.profilePic != "") {
            btn_delete_profile_pic.isEnabled = true
            Glide.with(requireContext())
                .load(args.profilePic)
                .into(img_profile)
        } else
            img_profile.setImageResource(R.drawable.ic_creative_person__)
        binding.userName.text = args.userName

        binding.btnDeleteProfilePic.setOnClickListener {
           viewModel.deleteProfilePhoto()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    private fun subscribeToObservers() {
        viewModel.deleteProfilePicStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {}
        ) {
            img_profile.setImageResource(R.drawable.ic_creative_person__)
        })
    }
}