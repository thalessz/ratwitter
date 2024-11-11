package com.thalessz.ratwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.thalessz.ratwitter.dao.PostDAO;
import com.thalessz.ratwitter.dao.UserDAO;
import com.thalessz.ratwitter.models.PostUser;
import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.retrofit.RetrofitClient;
import com.thalessz.ratwitter.utils.PostAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostUser> postUsers;
    private PostDAO postDAO;
    private UserDAO userDAO;
    private EditText edtConteudo;
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            String json = sharedPreferences.getString("user", null);

            if (json != null) {
                user = new Gson().fromJson(json, User.class);
            }

            if (user != null) {
                Toast.makeText(this, "Nome: " + user.getUsername(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
            }
        }

        recyclerView = findViewById(R.id.rcw_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postUsers = new ArrayList<>();
        postAdapter = new PostAdapter(postUsers, user.getId());
        recyclerView.setAdapter(postAdapter);

        userDAO = new UserDAO(RetrofitClient.getApiService());
        postDAO = new PostDAO(RetrofitClient.getApiService());

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchPosts());

        fetchPosts();

        Button btnRatear = findViewById(R.id.btnRatear);
        edtConteudo = findViewById(R.id.edtConteudo);
        btnRatear.setOnClickListener(v -> postarRateada());
    }

    private void postarRateada() {
        String content = edtConteudo.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um conteúdo.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDAO.fetchUserId(user.getUsername(), new UserDAO.FetchUserIdCallback() {
            @Override
            public Integer onSuccess(Integer userId) {
                Map<String,String> postContent = new HashMap<>();
                postContent.put("content", content);
                postContent.put("user_id", String.valueOf(userId));

                postDAO.addPost(postContent, new PostDAO.AddPostCallback() {
                    @Override
                    public void onSuccess(Map<String, String> response) {
                        edtConteudo.setText("");
                        Toast.makeText(MainActivity.this, "Publicação adicionada com sucesso", Toast.LENGTH_SHORT).show();
                        fetchPosts();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, "Algo errado aconteceu", Toast.LENGTH_SHORT).show();
                    }
                });
                return userId;
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FetchID", "onFailure: deu pau tentando pegar o id " + errorMessage );
                Toast.makeText(MainActivity.this, "Erro ao buscar ID do usuário.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPosts() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(true);

        postDAO.fetchPosts(new PostDAO.FetchPostsWithUsersCallback() {
            @Override
            public void onSuccess(List<PostUser> postsWithUsers) {
                postUsers.clear();
                postUsers.addAll(postsWithUsers);
                postAdapter.notifyDataSetChanged();

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("tela dos posts", "Erro ao buscar posts: " + errorMessage);

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                Toast.makeText(MainActivity.this, "Erro ao carregar posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}