package com.thalessz.ratwitter.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.thalessz.ratwitter.R
import com.thalessz.ratwitter.dao.PostDAO
import com.thalessz.ratwitter.models.PostUser
import com.thalessz.ratwitter.retrofit.RetrofitClient
import java.util.Locale

class VisualizarPost : AppCompatActivity() {
    private lateinit var tvwNome: TextView
    private lateinit var tvwUsername: TextView
    private lateinit var tvwPostContent: TextView
    private lateinit var tvwLikeCount: TextView
    private lateinit var btnLike: Button
    private lateinit var imageView: ImageView

    private lateinit var postUser: PostUser
    private lateinit var postDAO: PostDAO
    private var currentUserId: Int = 0 // ID do usuário logado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_post)

        tvwNome = findViewById(R.id.tvwNome)
        tvwUsername = findViewById(R.id.tvwUsername)
        tvwPostContent = findViewById(R.id.tvwPostContent)
        tvwLikeCount = findViewById(R.id.tvwLikeCount)
        btnLike = findViewById(R.id.btnLike)
        imageView = findViewById(R.id.imageView)

        postDAO = PostDAO(RetrofitClient.getApiService())

        val postUserJson = intent.getStringExtra("postUser")
        currentUserId = intent.getIntExtra("currentUID", 0) // Recebendo o currentUID

        if (postUserJson != null) {
            postUser = Gson().fromJson(postUserJson, PostUser::class.java)
            displayPostDetails(postUser)
        }

        btnLike.setOnClickListener {
            if (btnLike.text == "♥" || btnLike.text == "♡") {
                likePost(postUser.post.id)
            } else {
                unlikePost(postUser.post.id)
            }
        }

        val btnVoltar: ImageButton = findViewById(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun displayPostDetails(postUser: PostUser) {
        tvwNome.text = postUser.user.nome
        val username = "@" + postUser.user.username.lowercase(Locale.getDefault())
        tvwUsername.text = username
        tvwPostContent.text = postUser.post.content
        val likeCount = "${postUser.post.like_count} rateadas"
        tvwLikeCount.text = likeCount
        imageView.setImageResource(R.drawable.rato)

        checkIfLiked(postUser.post.id, currentUserId)
    }

    private fun checkIfLiked(postId: Int, userId: Int) {
        postDAO.checkIfLiked(postId, userId, object : PostDAO.CheckLikeCallback {
            override fun onSuccess(isLiked: Boolean) {
                btnLike.text = if (isLiked) "♥" else "♡"
            }

            override fun onFailure(errorMessage: String) {
                Log.e("VisualizarPost", "Erro ao verificar curtida: $errorMessage")
            }
        })
    }

    private fun likePost(postId: Int) {
        val userIdMap = mapOf("user_id" to currentUserId)
        postDAO.likePost(postId, userIdMap, object : PostDAO.LikePostCallback {
            override fun onSuccess(response: Map<String, String>) {
                Toast.makeText(this@VisualizarPost, response["message"], Toast.LENGTH_SHORT).show()
                val newLikeCount = "${postUser.post.like_count + 1} rateadas"
                tvwLikeCount.text = newLikeCount
                btnLike.text = "♥"
                postUser.post.like_count += 1
            }
            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@VisualizarPost, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun unlikePost(postId: Int) {
        postDAO.unlikePost(postId, currentUserId, object : PostDAO.LikePostCallback {
            override fun onSuccess(response: Map<String, String>) {
                Toast.makeText(this@VisualizarPost, response["message"], Toast.LENGTH_SHORT).show()
                val newLikeCount = "${postUser.post.like_count - 1} rateadas"
                tvwLikeCount.text = newLikeCount
                btnLike.text = "♡"
                postUser.post.like_count -= 1
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@VisualizarPost, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}