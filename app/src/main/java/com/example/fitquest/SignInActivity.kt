package com.example.fitquest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.fitquest.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val errorMessageTextView = binding.errorMessageTextView

        // Inicializar o AuthManager no método onCreate
        authManager = AuthManager(this)

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            authManager.signIn(email, password, signUpBefore = false) { success, errorMessage ->
                if (success) {
                    // Autenticação bem-sucedida, faça o que precisar aqui
                    Log.d("SignInActivity", "Autenticação bem-sucedida")

                    val intent = Intent(this@SignInActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Opcional: finalize esta atividade se não quiser que o usuário volte para ela pressionando o botão de voltar
                } else {
                    // Autenticação falhou, trate o erro ou exiba uma mensagem para o usuário
                    errorMessage?.let {
                        errorMessageTextView.text = "Incorrect email or password. Please try again."
                        errorMessageTextView.visibility = View.VISIBLE
                    }
                }
            }
        }

        // Set OnClickListener for the "Not yet registered? Register here" text
        binding.signupRedirectText.setOnClickListener {
            // Start SignUpActivity when the text is clicked
            val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
