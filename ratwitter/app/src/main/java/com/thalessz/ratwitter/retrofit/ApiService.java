package com.thalessz.ratwitter.retrofit;

import com.thalessz.ratwitter.models.Post;
import com.thalessz.ratwitter.models.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface ApiService {

    @POST("/users/login")
    Call<Map<String, Object>> login(@Body Map<String, String> credentials);

    @POST("/users/cadastro")
    Call<Map<String, String>> cadastro(@Body User userDetails);

    @GET("/posts/fetch_posts")
    Call<List<Post>> fetchPosts();

    @GET("/posts/fetch_by_uid/{uid}")
    Call<List<Post>> fetchPostByUid(@Path("uid") int uid);

    @POST("/posts/add_post")
    Call<Map<String, String>> addPost(@Body Map<String, String> postDetails);

    @POST("/posts/like_post/{post_id}")
    Call<Map<String, String>> likePost(@Path("post_id") int postId, @Body Map<String, Integer> userId);

    @DELETE("/posts/unlike_post/{post_id}")
    Call<Map<String, String>> unlikePost(@Path("post_id") int postId, @Query("user_id") int userId);

    @GET("/posts/check_if_liked/{post_id}")
    Call<List<Map<String, Integer>>> checkIfLiked(@Path("post_id") int postId, @Query("user_id") int userId);

    @GET("/users/fetch_user/{id}")
    Call<User> fetchUser(@Path("id") int id);

    @GET("/users/fetch_uid/{username}")
    Call<Map<String, Integer>> fetchUserId(@Path("username") String username);

}
