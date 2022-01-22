package com.ari_d.justeatit.Dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.Adapters.CommentAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.ViewModels.DetailsViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.android.synthetic.main.fragment_favorites.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialog : DialogFragment(R.layout.fragment_comments) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var commentAdapter: CommentAdapter

    private val args: CommentDialogArgs by navArgs()

    private val viewModel: DetailsViewModel by viewModels()

    private lateinit var dialogView: View
    private lateinit var rvComments: RecyclerView
    private lateinit var etComment: EditText
    private lateinit var btnComment: Button
    private lateinit var commentProgressBar: ProgressBar
    private lateinit var empty_layout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return dialogView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_comments,
            null
        )
        rvComments = dialogView.findViewById(R.id.rvComments)
        etComment = dialogView.findViewById(R.id.etComment)
        btnComment = dialogView.findViewById(R.id.btnComment)
        commentProgressBar = dialogView.findViewById(R.id.commentProgressBar)
        empty_layout = dialogView.findViewById(R.id.empty_layout)
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comments)
            .setView(dialogView)
            .create()
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        getComments()

        btnComment.setOnClickListener {
            val commentText = etComment.text.toString()
            viewModel.createComment(commentText, args.productId)
            etComment.text?.clear()
        }

        commentAdapter.setOnDeleteCommentClickListener { comment ->
            viewModel.deleteComment(comment)
        }
    }

    @InternalCoroutinesApi
    private fun getComments() {
        lifecycleScope.launch {
            viewModel.getPagingFlow(args.productId).collect {
                commentAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            commentAdapter.loadStateFlow.collectLatest {
                if (it.refresh is LoadState.Loading || it.append is LoadState.Loading) {
                    rvComments.isVisible = true
                    rvComments2.isVisible = false
                    commentProgressBar.isVisible = true
                } else if (it.refresh is LoadState.Error) {
                    commentProgressBar.isVisible = false
                    rvComments2.isVisible = true
                    rvComments.isVisible = false
                    empty_layout.isVisible = true
                } else if (it.refresh is LoadState.NotLoading || it.append is LoadState.NotLoading) {
                    commentProgressBar.isVisible = false
                    rvComments.isVisible = true
                    rvComments2.isVisible = false
                }
            }
        }
    }

    @InternalCoroutinesApi
    private fun subscribeToObservers() {
        viewModel.createCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                snackbar(it)
                btnComment.isEnabled = true
            },
            onLoading = {
                commentProgressBar.isVisible = true
                btnComment.isEnabled = false
                empty_layout.isVisible = false
            }
        ) { comment ->
            commentProgressBar.isVisible = false
            btnComment.isEnabled = true
            commentAdapter.refresh()
        })
        viewModel.deleteCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                commentProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { commentProgressBar.isVisible = true }
        ) { comment ->
            commentProgressBar.isVisible = false
            commentAdapter.refresh()
        })
    }

    private fun setupRecyclerView() = rvComments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}