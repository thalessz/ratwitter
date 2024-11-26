package com.thalessz.ratwitter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.thalessz.ratwitter.dao.PostDAO
import com.thalessz.ratwitter.dao.UserDAO
import com.thalessz.ratwitter.models.PostUser
import com.thalessz.ratwitter.models.User
import com.thalessz.ratwitter.retrofit.RetrofitClient
import com.thalessz.ratwitter.utils.PostAdapter

class MeuPerfil : AppCompatActivity() {
    private val postUsers: MutableList<PostUser> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meu_perfil)

        setupWindowInsets()

        val sharedPreferences: SharedPreferences = getSharedPreferences("Config", MODE_PRIVATE)
        user = getUserFromPreferences(sharedPreferences)

        if (user != null) {
            Toast.makeText(this, "Nome: ${user?.username}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val txtProfileName: TextView = findViewById(R.id.txt_profile_name)
        val txtProfileUsername: TextView = findViewById(R.id.txt_profile_username)
        recyclerView = findViewById(R.id.rcw_posts_profile)

        txtProfileName.text = user?.nome
        txtProfileUsername.text = "@${user?.username}"

        fetchUserIdAndPosts(user?.username)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.nav_perfil)
        bottomNavigation.selectedItemId = R.id.navigation_profile

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun fetchUserIdAndPosts(username: String?) {
        UserDAO.fetchUserId(username, object : UserDAO.FetchUserIdCallback {
            override fun onSuccess(userId: Int) {
                fetchPostsByUid(userId)
                recyclerView.layoutManager = LinearLayoutManager(this@MeuPerfil)
                postAdapter = PostAdapter(postUsers, userId)
                recyclerView.adapter = postAdapter
            }

            override fun onFailure(errorMessage: String) {
                Log.e("FetchID", "Erro ao buscar ID do usuário: $errorMessage")
                Toast.makeText(this@MeuPerfil, "Erro ao buscar ID do usuário.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPostsByUid(userId: Int) {
        val postDAO = PostDAO(RetrofitClient.getApiService())

        postDAO.fetchPostsByUid(userId, object : PostDAO.FetchByUidCallback {
            override fun onSuccess(postWithUsers: MutableList<PostUser>?) {
                postWithUsers?.let {
                    postUsers.clear()
                    postUsers.addAll(it)
                    postAdapter.notifyDataSetChanged()
                } ?: run {
                    Log.e("FetchPosts", "A lista de posts com usuários é nula.")
                }
            }

            override fun onFailure(errorMessage: String?) {
                Toast.makeText(this@MeuPerfil, "$errorMessage", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getUserFromPreferences(sharedPreferences: SharedPreferences): User? {
        val json = sharedPreferences.getString("user", null)
        return if (json != null) Gson().fromJson(json, User::class.java) else null
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_tela_inicial, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                Toast.makeText(this, "Logout selecionado", Toast.LENGTH_SHORT).show()
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}