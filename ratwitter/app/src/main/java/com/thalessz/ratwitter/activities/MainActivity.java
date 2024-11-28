package com.thalessz.ratwitter.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.thalessz.ratwitter.R;
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
    private final List<PostUser> postUsers = new ArrayList<>();
    private PostDAO postDAO;
    private UserDAO userDAO;
    private EditText edtConteudo;
    private User user;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setupWindowInsets();

        // Configuração do Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_perfil);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true; // Já estamos na Home, não faz nada
                case R.id.navigation_profile:
                    Intent intent = new Intent(MainActivity.this, MeuPerfil.class);
                    intent.putExtra("user", new Gson().toJson(user));
                    startActivity(intent);
                    return true;
            }
            return false;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);
        if (!isLoggedIn) {
            navigateToLogin();
            return; // Saia do método se não estiver logado
        }

        user = getUserFromPreferences(sharedPreferences);
        if (user != null) {
            Toast.makeText(this, "Nome: " + user.getUsername(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
        }

        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();

        Button btnRatear = findViewById(R.id.btnRatear);
        edtConteudo = findViewById(R.id.edtConteudo);
        btnRatear.setOnClickListener(v -> postarRateada());
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private User getUserFromPreferences(SharedPreferences sharedPreferences) {
        String json = sharedPreferences.getString("user", null);
        return json != null ? new Gson().fromJson(json, User.class) : null;
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.rcw_posts);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        userDAO = new UserDAO(RetrofitClient.getApiService());
        postDAO = new PostDAO(RetrofitClient.getApiService());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fetchUid(); // Chama fetchUid para buscar e passar o UID ao adapter
    }

    private void fetchUid() {
        UserDAO.fetchUserId(user.getUsername(), new UserDAO.FetchUserIdCallback() {
            @Override
            public void onSuccess(Integer userId) {
                // Atualiza o adapter com o UID obtido
                postAdapter = new PostAdapter(postUsers, userId);
                recyclerView.setAdapter(postAdapter); // Configura o adapter no RecyclerView
                fetchPosts(); // Carregar posts após configurar o adapter
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(MainActivity.this, "Erro ao buscar ID do usuário: " + errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("FetchID", errorMessage);
            }
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchPosts);
    }

    private void postarRateada() {
        String content = edtConteudo.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um conteúdo.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDAO.fetchUserId(user.getUsername(), new UserDAO.FetchUserIdCallback() {
            @Override
            public void onSuccess(Integer userId) {
                Map<String, String> postContent = new HashMap<>();
                postContent.put("content", content);
                postContent.put("user_id", String.valueOf(userId));

                postDAO.addPost(postContent, new PostDAO.AddPostCallback() {
                    @Override
                    public void onSuccess(Map<String, String> response) {
                        edtConteudo.setText("");
                        Toast.makeText(MainActivity.this, "Publicação adicionada com sucesso", Toast.LENGTH_SHORT).show();
                        fetchPosts(); // Atualiza a lista de posts após adicionar um novo
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FetchID", "Erro ao buscar ID do usuário: " + errorMessage);
                Toast.makeText(MainActivity.this, "Erro ao buscar ID do usuário.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPosts() {
        recyclerView.setVisibility(View.GONE); // Esconde o RecyclerView enquanto carrega os dados
        swipeRefreshLayout.setRefreshing(true); // Ativa a animação de carregamento

        postDAO.fetchPosts(new PostDAO.FetchPostsWithUsersCallback() {
            @Override
            public void onSuccess(List<PostUser> postsWithUsers) {
                postUsers.clear(); // Limpa a lista anterior de posts
                postUsers.addAll(postsWithUsers); // Adiciona os novos posts à lista
                postAdapter.notifyDataSetChanged(); // Notifica o adapter sobre as mudanças nos dados

                swipeRefreshLayout.setRefreshing(false); // Desativa a animação de carregamento
                recyclerView.setVisibility(View.VISIBLE); // Mostra o RecyclerView novamente
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("FetchPosts", "Erro ao buscar posts: " + errorMessage);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Erro ao carregar posts.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}