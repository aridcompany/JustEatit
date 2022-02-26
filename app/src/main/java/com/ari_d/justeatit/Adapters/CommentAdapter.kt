package com.ari_d.justeatit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ari_d.justeatit.R
import com.ari_d.justeatit.data.entities.Comment
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_comment.view.*
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Comment, CommentAdapter.CommentViewHolder>(Companion) {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCommentUsername: TextView = itemView.tvCommentUsername
        val tvComment: TextView = itemView.tvComment
        val ibDeleteComment: ImageButton = itemView.ibDeleteComment
        val ivCommentProfilePic: CircleImageView = itemView.ivCommentUserProfilePicture
    }

    companion object : DiffUtil.ItemCallback<Comment>() {
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position) ?: return
        holder.apply {
            ibDeleteComment.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!
            ibDeleteComment.setImageResource(R.drawable.ic_baseline_delete_forever_24)

            tvComment.text = comment.comment
            tvCommentUsername.text = comment.name
            if (comment.profile_pic != "")
                glide.load(comment.profile_pic)
                    .into(ivCommentProfilePic)
            else
                ivCommentProfilePic.setImageResource(R.drawable.ic_creative_person__1_)
            ibDeleteComment.setOnClickListener {
                onDeleteCommentClickListener?.let { click ->
                    click(comment)
                }
            }
        }
    }
    private var onDeleteCommentClickListener: ((Comment) -> Unit)? = null

    fun setOnDeleteCommentClickListener(listener: (Comment) -> Unit) {
        onDeleteCommentClickListener = listener
    }
}