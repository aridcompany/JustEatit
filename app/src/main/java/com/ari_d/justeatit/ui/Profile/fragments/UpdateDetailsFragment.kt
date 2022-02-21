@file:Suppress("DEPRECATION")

package com.ari_d.justeatit.ui.Profile.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.Constants
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Profile.ViewModels.ProfileViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_update_details.*
import kotlinx.android.synthetic.main.fragment_update_details.btn_back
import kotlinx.android.synthetic.main.fragment_update_details.progressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UpdateDetailsFragment : Fragment(R.layout.fragment_update_details) {

    val viewModel: ProfileViewModel by viewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser!!
    private val GALLERY_REQUEST_CODE = 1234
    private var user_name: String = ""
    private var img_uri: String = ""
    private var img_url: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setNameandEmail()
        subscribeToObservers()

        val profile_pic = view.findViewById<CircleImageView>(R.id.img_profile)
        ViewCompat.setTransitionName(profile_pic, "item_image")
        TextInputEditText_email.setText(currentUser.email)

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                UpdateDetailsFragmentDirections.actionUpdateDetailsFragmentToMainProfileFragment()
            )
        }

        edit_profile_pic.setOnClickListener {
            pickFromGallery()
        }
        img_profile.setOnClickListener {
            updateProfilePic()
        }

        var job: Job? = null
        TextInputEditText_name.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(Constants.SEARCH_TIME_DELAY)
                editable?.let {
                    txt_name.text = it.toString()
                }
            }
        }

        btn_update_details.setOnClickListener {
            viewModel.updateUserDetails(
                TextInputEditText_name.text.toString(),
                img_uri
            )
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

    private fun updateProfilePic() {
        if (user_name != "" || img_url != "") {
            val extras = FragmentNavigatorExtras(txt_name to "img_profile_big")
            findNavController().navigate(
                R.id.globalActionToViewProfilePicture,
                Bundle().apply {
                    putString(
                        "user_name",
                        user_name
                    )
                    putString(
                        "profile_pic",
                        img_url
                    )
                },
                null,
                extras
            )
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun subscribeToObservers() {
        viewModel.updateUserDetailsStautus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_update_details.isVisible = true
                snackbar(it)
            },
            onLoading = {
                progressBar.isVisible = true
                btn_update_details.isVisible = false
            }
        ) {
            progressBar.isVisible = false
            btn_update_details.isVisible = true
            viewModel.setNameandEmail()
        })
        viewModel.setNameStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressBar.isVisible = false
                btn_update_details.isVisible = true
                snackbar(it)
            },
            onLoading = {
                progressBar.isVisible = true
                btn_update_details.isVisible = false
            }
        ) { user ->
            txt_email.text = currentUser.email
            if (user.name == "null")
                txt_name.text = getString(R.string.title_welcome)
            else {
                txt_name.text = user.name
                TextInputEditText_name.setText(user.name)
                user_name = user.name
            }
            if (user.profile_pic != "") {
                Glide.with(requireContext())
                    .load(user.profile_pic)
                    .into(img_profile)
                img_url = user.profile_pic
            }
            progressBar.isVisible = false
            btn_update_details.isVisible = true
        })
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(img_profile)
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1080, 1080)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                } else {
                    Log.e(TAG, "Image selection error: couldn't select that image from memory.")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                    img_uri = result.uri.toString()
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.error}")
                }
            }
        }
    }
}