package com.thalessz.ratwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private User user; // Mover a declaração para a classe
    private boolean isLoading = false; // Para controlar o estado de carregamento
    private int currentPage = 1; // Para controlar a página atual

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
                user = new Gson().fromJson(json, User.class); // Atribuir diretamente à variável de instância
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
        postAdapter = new PostAdapter(postUsers);
        recyclerView.setAdapter(postAdapter);

        userDAO = new UserDAO(RetrofitClient.getApiService());
        postDAO = new PostDAO(RetrofitClient.getApiService());

        fetchPosts(currentPage); // Carregar a primeira página

        Button btnRatear = findViewById(R.id.btnRatear);
        edtConteudo = findViewById(R.id.edtConteudo);

        btnRatear.setOnClickListener(v -> postarRateada());

        // Listener para o NestedScrollView
        findViewById(R.id.nested_scroll_view).setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isLoading && (v.getChildAt(0).getBottom() <= (v.getHeight() + scrollY))) {
                    // Carregar mais dados quando chegar ao final da lista
                    currentPage++;
                    fetchPosts(currentPage);
                }
            }
        });
    }

    private void postarRateada() {
        String content = edtConteudo.getText().toString();

        // Verifica se o conteúdo não está vazio
        if (content.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um conteúdo.", Toast.LENGTH_SHORT).show();
            return; // Retorna se o conteúdo estiver vazio
        }

        // Chama a função para buscar o ID do usuário
        UserDAO.fetchUserId(user.getUsername(), new UserDAO.FetchUserIdCallback() {
            @Override
            public Integer onSuccess(Integer userId) {
                Map<String,String> postContent = new HashMap<>();
                postContent.put("content", content);
                postContent.put("user_id", String.valueOf(userId));

                postDAO.addPost(postContent, new PostDAO.AddPostCallback() {
                    @Override
                    public void onSuccess(Map<String, String> response) {
                        Toast.makeText(MainActivity.this, "Publicação adicionada com sucesso", Toast.LENGTH_SHORT).show();
                        fetchPosts(currentPage); // Atualiza a lista após adicionar um novo post
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

    private void fetchPosts(int page) {
        isLoading = true; // Indica que estamos carregando dados

        postDAO.fetchPosts(new PostDAO.FetchPostsWithUsersCallback() {
            @Override
            public void onSuccess(List<PostUser> postsWithUsers) {
                if (page == 1) { // Se for a primeira página, limpa a lista existente
                    postUsers.clear();
                }
                postUsers.addAll(postsWithUsers); // Adiciona os novos posts à lista existente
                postAdapter.notifyDataSetChanged(); // Notifica o adaptador sobre as mudanças

                isLoading = false; // Indica que o carregamento foi concluído
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e("tela dos posts", "Erro ao buscar posts: " + errorMessage);
                isLoading = false; // Indica que houve uma falha no carregamento
            }
        });
    }
}