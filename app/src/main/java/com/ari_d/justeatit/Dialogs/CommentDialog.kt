package com.ari_d.justeatit.Dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ari_d.justeatit.Adapters.CommentAdapter
import com.ari_d.justeatit.Extensions.snackbar
import com.ari_d.justeatit.R
import com.ari_d.justeatit.other.EventObserver
import com.ari_d.justeatit.ui.Details.ViewModels.DetailsViewModel
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_comments.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@AndroidEntryPoint
class CommentDialog : BottomSheetDialogFragment() {

    @Inject
    lateinit var glide: RequestManager
    @Inject
    lateinit var commentAdapter: CommentAdapter
    private val args: CommentDialogArgs by navArgs()
    private val viewModel: DetailsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        getComments()

        tilComment.setEndIconOnClickListener {
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
                tilComment.setEndIconActivated(true)
            },
            onLoading = {
                commentProgressBar.isVisible = true
                tilComment.setEndIconActivated(false)
                empty_layout.isVisible = false
            }
        ) { comment ->
            commentProgressBar.isVisible = false
            tilComment.setEndIconActivated(true)
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