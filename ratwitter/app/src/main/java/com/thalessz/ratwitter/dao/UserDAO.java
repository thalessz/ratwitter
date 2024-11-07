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

        Call<Map<String, Object>> call = apiService.login(credentials);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Extraindo os dados do response
                    Map<String, Object> userData = response.body();

                    // Construindo o objeto User
                    String nome = (String) userData.get("NOME");
                    String userUsername = (String) userData.get("USERNAME");
                    String email = (String) userData.get("EMAIL");
                    String password = (String) userData.get("PASSWORD"); // Se necessário

                    User user = new User(nome, userUsername, email, password); // Construa o objeto User

                    // Chamando o callback com o objeto User
                    callback.onSuccess(user);
                } else {
                    callback.onFailure("Login inválido");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
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

    public static int fetchUserId(String username, final FetchUserIdCallback callback) {
        Call<Map<String, Integer>> call = apiService.fetchUserId(username);
        call.enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Integer userId = response.body().get("user_id");
                    callback.onSuccess(userId);
                } else {
                    callback.onFailure("Usuário não encontrado");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Integer>> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
        return 0;
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onFailure(String errorMessage);
    }

    public interface CadastroCallback {
        void onSuccess(Map<String, String> response);
        void onFailure(String errorMessage);
    }

    public interface FetchUserIdCallback {
        Integer onSuccess(Integer userId);
        void onFailure(String errorMessage);
    }
}
