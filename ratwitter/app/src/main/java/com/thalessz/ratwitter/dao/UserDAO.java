package com.thalessz.ratwitter.dao;

import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.retrofit.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDAO {
    private static ApiService apiService = null;
    public UserDAO(ApiService apiService) {
        UserDAO.apiService = apiService;
    }
    public static void login(String username, String password, final LoginCallback callback) {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        Call<User> call = apiService.login(credentials);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Login inválido");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
    public static void cadastro(User user, final CadastroCallback callback) {
        Call<Map<String, String>> call = apiService.cadastro(user);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("Cadastro inválido");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }
    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }
    public interface CadastroCallback {
        void onSuccess(Map<String, String> response);
        void onFailure(String errorMessage);
    }
}
