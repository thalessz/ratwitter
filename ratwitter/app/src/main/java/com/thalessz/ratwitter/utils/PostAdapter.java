package com.thalessz.ratwitter.utils;

import static androidx.core.content.ContextCompat.startActivity;
import static com.thalessz.ratwitter.utils.DateFormatter.formatDate;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.thalessz.ratwitter.MainActivity;
import com.thalessz.ratwitter.MeuPerfil;
import com.thalessz.ratwitter.R;
import com.thalessz.ratwitter.VisualizarPost;
import com.thalessz.ratwitter.dao.PostDAO;
import com.thalessz.ratwitter.models.Post;
import com.thalessz.ratwitter.models.PostUser;
import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.retrofit.RetrofitClient;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostUser> postUsers;
    private int currentUID;
    private PostDAO postDAO;

    public PostAdapter(List<PostUser> postUsers, int currentUID) {
        this.postUsers = postUsers;
        this.postDAO = new PostDAO(RetrofitClient.getApiService());
        this.currentUID = currentUID;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_model, parent, false);
        return new PostViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        PostUser postUser = postUsers.get(position); // Obtém o PostUser atual
        holder.tvwNome.setText(postUser.getUser().getNome());
        holder.tvwUsername.setText("@" + postUser.getUser().getUsername().toLowerCase());
        holder.tvwPostContent.setText(postUser.getPost().getContent());
        holder.tvwLikeCount.setText(postUser.getPost().getLike_count() + " rateadas");
        holder.tvwDate.setText(formatDate(postUser.getPost().getCreated_at()));
        holder.imageView.setImageResource(R.drawable.rato);

        // Verifica se o post foi curtido pelo usuário atual
        checkIfLiked(postUser.getPost().getId(), currentUID, holder);

        // Configura o click listener para curtir/descurtir
        holder.btnLike.setOnClickListener(v -> {
            if (holder.btnLike.getText().equals("Curtir") || holder.btnLike.getText().equals("♡")) {
                Log.e("likePost", postUser.getPost().getId() + " " + currentUID);
                likePost(postUser.getPost().getId(), holder, postUser);
            } else {
                Log.e("unlike", postUser.getPost().getId() + " " + currentUID);
                unlikePost(postUser.getPost().getId(), holder, postUser);
            }
        });

        holder.imageView.setOnClickListener(v -> showProfile(postUser.getUser(), v));
        holder.tvwNome.setOnClickListener(v -> showProfile(postUser.getUser(), v));
        holder.tvwUsername.setOnClickListener(v -> showProfile(postUser.getUser(), v));

        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(v.getContext(), VisualizarPost.class);
            intent.putExtra("postUser", new Gson().toJson(postUser));
            intent.putExtra("currentUID", currentUID);
            v.getContext().startActivity(intent);
        });

    }

    private void showProfile(User user, View v) {
        Intent intent = new Intent(v.getContext(), MeuPerfil.class);
        intent.putExtra("user", new Gson().toJson(user));

        v.getContext().startActivity(intent);
    }


    private void checkIfLiked(int postId, int userId, PostViewHolder holder) {
        postDAO.checkIfLiked(postId, userId, new PostDAO.CheckLikeCallback() {
            @Override
            public void onSuccess(Boolean isLiked) {
                // Atualiza o texto do botão com base na verificação
                if (isLiked) {
                    holder.btnLike.setText("♥"); // Coração cheio se curtiu
                } else {
                    holder.btnLike.setText("♡"); // Coração vazio se não curtiu
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                // Trate o erro conforme necessário (ex: log ou mensagem para o usuário)
                Log.e("PostAdapter", "Erro ao verificar curtida: " + errorMessage);
            }
        });
    }

    private void likePost(int postId, PostViewHolder holder, PostUser postUser) {
        Map<String, Integer> userIdMap = new HashMap<>();
        userIdMap.put("user_id", currentUID); // ID do usuário logado

        postDAO.likePost(postId, userIdMap, new PostDAO.LikePostCallback() {
            @Override
            public void onSuccess(Map<String, String> response) {
                Toast.makeText(holder.itemView.getContext(), response.get("message"), Toast.LENGTH_SHORT).show();
                // Atualiza a contagem de likes
                int newLikeCount = postUser.getPost().getLike_count() + 1; // Aumenta a contagem
                holder.tvwLikeCount.setText(newLikeCount + " rateadas");
                holder.btnLike.setText("♥"); // Muda para o estado "curtido"
                postUser.getPost().setLike_count(newLikeCount); // Atualiza o modelo de dados
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(holder.itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlikePost(int postId, PostViewHolder holder, PostUser postUser) {
        postDAO.unlikePost(postId, currentUID, new PostDAO.LikePostCallback() {
            @Override
            public void onSuccess(Map<String, String> response) {
                Toast.makeText(holder.itemView.getContext(), response.get("message"), Toast.LENGTH_SHORT).show();
                // Atualiza a contagem de likes
                int newLikeCount = postUser.getPost().getLike_count() - 1; // Diminui a contagem
                holder.tvwLikeCount.setText(newLikeCount + " rateadas");
                holder.btnLike.setText("♡"); // Muda para o estado "não curtido"
                postUser.getPost().setLike_count(newLikeCount); // Atualiza o modelo de dados
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(holder.itemView.getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return postUsers.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tvwNome, tvwUsername, tvwPostContent, tvwDate, tvwLikeCount;
        Button btnLike;
        ImageView imageView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tvwNome = itemView.findViewById(R.id.tvwNome);
            tvwUsername = itemView.findViewById(R.id.tvwUsername);
            tvwPostContent = itemView.findViewById(R.id.tvwPostContent);
            tvwDate = itemView.findViewById(R.id.tvwDate);
            tvwLikeCount = itemView.findViewById(R.id.tvwLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}
