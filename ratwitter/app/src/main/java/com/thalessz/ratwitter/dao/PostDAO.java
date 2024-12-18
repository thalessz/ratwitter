package com.thalessz.ratwitter.dao;

import android.util.Log;

import com.thalessz.ratwitter.models.Post;
import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.models.PostUser;
import com.thalessz.ratwitter.retrofit.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDAO {
    private final ApiService apiService;

    public PostDAO(ApiService apiService) {
        this.apiService = apiService;
    }

    public void fetchPosts(final FetchPostsWithUsersCallback callback) {
        Call<List<Post>> call = this.apiService.fetchPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    List<PostUser> postUsers = new ArrayList<>();
                    List<Call<User>> userCalls = new ArrayList<>();

                    for (Post post : posts) {
                        Call<User> userCall = apiService.fetchUser(post.getUser_id());
                        userCalls.add(userCall);

                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> userResponse) {
                                if (userResponse.isSuccessful() && userResponse.body() != null) {
                                    User user = userResponse.body();
                                    postUsers.add(new PostUser(post, user));
                                } else {
                                    // Adiciona uma mensagem de erro se o usuário não for encontrado
                                    callback.onFailure("Falha ao buscar usuário para o post ID: " + post.getId());
                                }

                                // Verifica se todos os usuários foram carregados
                                if (postUsers.size() + 1 == posts.size()) { // +1 para incluir falhas de usuários
                                    callback.onSuccess(postUsers);
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                callback.onFailure("Erro ao buscar usuário: " + t.getMessage());
                            }
                        });
                    }
                } else {
                    callback.onFailure("Falha ao buscar posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onFailure("Erro ao buscar posts: " + t.getMessage());
            }
        });
    }

    public void fetchPostsByUid(int uid, final FetchByUidCallback callback) {
        Call<List<Post>> call = this.apiService.fetchPostByUid(uid);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Post> posts = response.body();
                    List<PostUser> postUsers = new ArrayList<>();
                    List<Call<User>> userCalls = new ArrayList<>();

                    for (Post post : posts) {
                        Call<User> userCall = apiService.fetchUser(post.getUser_id());
                        userCalls.add(userCall);

                        userCall.enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> userResponse) {
                                if (userResponse.isSuccessful() && userResponse.body() != null) {
                                    User user = userResponse.body();
                                    postUsers.add(new PostUser(post, user));
                                } else {
                                    // Adiciona uma mensagem de erro se o usuário não for encontrado
                                    callback.onFailure("Falha ao buscar usuário para o post ID: " + post.getId());
                                }

                                // Verifica se todos os usuários foram carregados
                                if (postUsers.size() + 1 == posts.size()) { // +1 para incluir falhas de usuários
                                    callback.onSuccess(postUsers);
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                callback.onFailure("Erro ao buscar usuário: " + t.getMessage());
                            }
                        });
                    }
                } else {
                    callback.onFailure("Falha ao buscar posts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                callback.onFailure("Erro ao buscar posts: " + t.getMessage());
            }
        });
    }

    public void addPost(Map<String, String> postData, final AddPostCallback callback) {
        Call<Map<String, String>> call = this.apiService.addPost(postData);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Falha ao adicionar post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure("Erro ao adicionar post: " + t.getMessage());
            }
        });
    }

    // Método para curtir um post
    public void likePost(int postId, Map<String, Integer> userIdMap, final LikePostCallback callback) {
        Call<Map<String, String>> call = this.apiService.likePost(postId, userIdMap);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Falha ao curtir post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure("Erro ao curtir post: " + t.getMessage());
            }
        });
    }

    // Método para descurtir um post
    public void unlikePost(int postId, int userId, final LikePostCallback callback) {
        Call<Map<String, String>> call = this.apiService.unlikePost(postId, userId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    // Log da resposta para depuração
                    Log.e("unlikePost", "Falha ao descurtir post: " + response.message());
                    callback.onFailure("Falha ao descurtir post: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                Log.e("unlikePost", "Erro ao descurtir post: " + t.getMessage());
                callback.onFailure("Erro ao descurtir post: " + t.getMessage());
            }
        });
    }

    // Novo método para verificar se o post foi curtido pelo usuário
    public void checkIfLiked(int postId, int userId, final CheckLikeCallback callback) {
        Call<List<Map<String, Integer>>> call = this.apiService.checkIfLiked(postId, userId);
        call.enqueue(new Callback<List<Map<String, Integer>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Integer>>> call, Response<List<Map<String, Integer>>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Extraindo o valor "is_liked" da resposta
                    Integer isLikedValue = response.body().get(0).get("is_liked");
                    Boolean isLiked = (isLikedValue != null && isLikedValue == 1);
                    callback.onSuccess(isLiked);
                } else {
                    callback.onFailure("Falha ao verificar curtida: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Integer>>> call, Throwable t) {
                callback.onFailure("Erro ao verificar curtida: " + t.getMessage());
            }
        });
    }

    // Nova interface de callback para posts com usuários
    public interface FetchPostsWithUsersCallback {
        void onSuccess(List<PostUser> postsWithUsers);
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

    // Nova interface de callback para verificar likes
    public interface CheckLikeCallback {
        void onSuccess(Boolean isLiked);
        void onFailure(String errorMessage);
    }

    public interface FetchByUidCallback {
        void onSuccess(List<PostUser> postWithUsers);
        void onFailure(String errorMessage);
    }

}
