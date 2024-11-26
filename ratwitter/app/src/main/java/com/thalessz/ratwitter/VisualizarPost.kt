package com.thalessz.ratwitter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.thalessz.ratwitter.dao.PostDAO
import com.thalessz.ratwitter.models.PostUser
import com.thalessz.ratwitter.retrofit.RetrofitClient

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

        // Recupera o PostUser e currentUID passados pela Intent
        val postUserJson = intent.getStringExtra("postUser")
        currentUserId = intent.getIntExtra("currentUID", 0) // Recebendo o currentUID

        if (postUserJson != null) {
            postUser = Gson().fromJson(postUserJson, PostUser::class.java)
            displayPostDetails(postUser)
        }

        // Configura o botão de curtir/descurtir
        btnLike.setOnClickListener {
            if (btnLike.text == "♥" || btnLike.text == "♡") {
                likePost(postUser.post.id)
            } else {
                unlikePost(postUser.post.id)
            }
        }

        var btnVoltar: ImageButton = findViewById(R.id.btnVoltar);
        btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun displayPostDetails(postUser: PostUser) {
        tvwNome.text = postUser.user.nome
        tvwUsername.text = "@" + postUser.user.username.toLowerCase()
        tvwPostContent.text = postUser.post.content
        tvwLikeCount.text = "${postUser.post.like_count} rateadas"
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
                val newLikeCount = postUser.post.like_count + 1
                tvwLikeCount.text = "$newLikeCount rateadas"
                btnLike.text = "♥"
                postUser.post.like_count = newLikeCount
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
                val newLikeCount = postUser.post.like_count - 1
                tvwLikeCount.text = "$newLikeCount rateadas"
                btnLike.text = "♡"
                postUser.post.like_count = newLikeCount
            }

            override fun onFailure(errorMessage: String) {
                Toast.makeText(this@VisualizarPost, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}