package com.thalessz.ratwitter.utils;

import static com.thalessz.ratwitter.utils.DateFormatter.formatDate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thalessz.ratwitter.R;
import com.thalessz.ratwitter.models.PostUser;

import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<PostUser> postUsers;

    public PostAdapter(List<PostUser> postUsers) {
        this.postUsers = postUsers;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_model, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        PostUser postUser = postUsers.get(position);
        holder.tvwNome.setText(postUser.getUser().getNome());
        holder.tvwUsername.setText("@"+ postUser.getUser().getUsername().toLowerCase());
        holder.tvwPostContent.setText(postUser.getPost().getContent());
        holder.tvwLikeCount.setText(postUser.getPost().getLike_count() + " rateadas");
        holder.tvwDate.setText(formatDate(postUser.getPost().getCreated_at()));
        holder.imageView.setImageResource(R.drawable.rato);
        holder.btnLike.setOnClickListener(v->likePost(postUser.getPost().getId()));
    }

    private void likePost(int id) {
        //TODO
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
