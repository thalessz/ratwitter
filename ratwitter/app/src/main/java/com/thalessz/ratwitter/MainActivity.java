package com.thalessz.ratwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thalessz.ratwitter.dao.PostDAO;
import com.thalessz.ratwitter.models.PostUser;
import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.retrofit.ApiService; // Certifique-se de importar ApiService
import com.thalessz.ratwitter.retrofit.RetrofitClient;
import com.thalessz.ratwitter.utils.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<PostUser> postUsers;
    private PostDAO postDAO; // Adicione esta linha

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
        }

        String json = sharedPreferences.getString("user", null);
        if (json != null) {
            User user = new Gson().fromJson(json, User.class);
            // Você pode usar o objeto user aqui se necessário
        }

        recyclerView = findViewById(R.id.rcw_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postUsers = new ArrayList<>();
        postAdapter = new PostAdapter(postUsers);
        recyclerView.setAdapter(postAdapter);

        // Inicialize o PostDAO passando a instância do ApiService
        postDAO = new PostDAO(RetrofitClient.getApiService()); // Substitua por sua instância real do ApiService
        fetchPosts();
    }

    private void fetchPosts() {
        postDAO.fetchPosts(new PostDAO.FetchPostsWithUsersCallback() {
            @Override
            public void onSuccess(List<PostUser> postsWithUsers) {
                postUsers.clear();
                postUsers.addAll(postsWithUsers);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("MainActivity", "Erro ao buscar posts: " + errorMessage);
            }
        });
    }
}
