package com.ari_d.justeatit.ui.Profile.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ari_d.justeatit.Extensions.alertDialog
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
import kotlinx.android.synthetic.main.fragment_update_details.*
import kotlinx.android.synthetic.main.main_profile_fragment.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UpdateDetailsFragment : Fragment(R.layout.fragment_update_details) {

    val viewModel: ProfileViewModel by viewModels()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser!!
    var img_uri: String = ""
    private val GALLERY_REQUEST_CODE = 1234

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        TextInputEditText_email.setText(currentUser.email)

        btn_back.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else findNavController().navigate(
                UpdateDetailsFragmentDirections.actionUpdateDetailsFragmentToMainProfileFragment()
            )
        }

        edit_profile_pic.setOnClickListener {
           updateProfilePic()
        }
        img_profile.setOnClickListener {
            updateProfilePic()
        }

        var job: Job? = null
        TextInputEditText_email.addTextChangedListener { editable ->
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

    private fun updateProfilePic() {

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
            alertDialog(
                getString(R.string.title_successfully_updated_details),
                getString(R.string.title_details_updated),
                requireActivity().resources.getDrawable(R.drawable.ic_update_details_icon)
            )
        })
        viewModel.deleteProfilePicStatus.observe(viewLifecycleOwner, EventObserver(
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
            img_profile.setImageResource(R.drawable.ic_creative_person__1_)
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
            TextInputEditText_name.setText(user.name)
            txt_email.text = currentUser.email
            if (user.name == "null")
                txt_name.text = getString(R.string.title_welcome)
            else
                txt_name.text = user.name
           if (user.profile_pic != "") {
               Glide.with(this)
                   .load(user.profile_pic)
                   .into(img_profile)
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