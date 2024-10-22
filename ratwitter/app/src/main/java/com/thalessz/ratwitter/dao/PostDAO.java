package com.thalessz.ratwitter.dao;

import com.thalessz.ratwitter.models.Post;
import com.thalessz.ratwitter.retrofit.ApiService;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDAO {
    private ApiService apiService;
    public PostDAO(ApiService apiService) {
        this.apiService = apiService;
    }
    public void fetchPosts(final FetchPostsCallback callback) {
        Call<List<Post>> call = this.apiService.fetchPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Falha ao buscar posts");
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
    public void addPost(Post post, final AddPostCallback callback) {
        Call<Map<String, String>> call = this.apiService.addPost(post);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Falha ao adicionar post");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
    public void likePost(int postId, final LikePostCallback callback) {
        Call<Map<String, String>> call = this.apiService.likePost(postId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Falha ao curtir post");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }

    public interface FetchPostsCallback {
        void onSuccess(List<Post> posts);
        void onFailure(String errorMessage);
    }

    public interface AddPostCallback {
        void onSuccess(Map<String, String> response);
        void onFailure(String errorMessage);
    }

    public interface LikePostCallback {
        void onSuccess(Map<String, String> response);
        void onFailure(String errorMessage);
    }
}
