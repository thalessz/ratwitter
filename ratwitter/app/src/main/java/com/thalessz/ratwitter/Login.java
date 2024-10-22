package com.thalessz.ratwitter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.thalessz.ratwitter.dao.UserDAO;
import com.thalessz.ratwitter.models.User;
import com.thalessz.ratwitter.retrofit.ApiService;
import com.thalessz.ratwitter.retrofit.RetrofitClient;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ApiService apiService = RetrofitClient.getApiService();
        UserDAO userDAO = new UserDAO(apiService);

        EditText txtUsuario = findViewById(R.id.txtUsuário);
        EditText txtSenha = findViewById(R.id.txtSenha);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v->{
            String username = txtUsuario.getText().toString();
            String senha = txtSenha.getText().toString();

            userDAO.login(username, senha, new UserDAO.LoginCallback() {
                @Override
                public void onSuccess(User user) {

                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        });
    }
}