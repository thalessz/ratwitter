package com.thalessz.ratwitter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.thalessz.ratwitter.dao.UserDAO;
import com.thalessz.ratwitter.models.User;

import java.util.Map;

public class Cadastro extends AppCompatActivity {
    EditText txtCadastroNome;
    EditText txtCadastroEmail;
    EditText txtCadastroSenha;
    EditText txtCadastroUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtCadastroNome = findViewById(R.id.txtCadastroNome);
        txtCadastroEmail = findViewById(R.id.txtCadastroEmail);
        txtCadastroSenha = findViewById(R.id.txtCadastroSenha);
        txtCadastroUsername = findViewById(R.id.txtCadastroUsername);
        TextView txtLogin = findViewById(R.id.txtLogin);
        Button btnCadastrar = findViewById(R.id.btnCadastrar);

        txtLogin.setOnClickListener(v->finish());
        btnCadastrar.setOnClickListener(v-> cadastrarUsuario());

    }

    private void cadastrarUsuario() {
        String nome = txtCadastroNome.getText().toString();
        String username = txtCadastroUsername.getText().toString();
        String senha = txtCadastroSenha.getText().toString();
        String email = txtCadastroSenha.getText().toString();

        User userDetails = new User(nome,username,senha,email);
        UserDAO.cadastro(userDetails, new UserDAO.CadastroCallback() {
            @Override
            public void onSuccess(Map<String, String> response) {
                Toast.makeText(Cadastro.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                redirectLogin(userDetails);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Cadastro.this, "Erro ao se cadastrar, tente novamente: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void redirectLogin(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        UserDAO.login(username, password, new UserDAO.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                SharedPreferences sharedPreferences = getSharedPreferences("Config", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(user);
                editor.putString("user", json);
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                Intent intent = new Intent(Cadastro.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("user", user);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Cadastro.this, "Erro ao se cadastrar, tente novamente: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }
}