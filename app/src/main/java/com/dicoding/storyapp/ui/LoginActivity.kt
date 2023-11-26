package com.dicoding.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.dicoding.storyapp.remote.retrofit.ApiClient
import com.dicoding.storyapp.remote.retrofit.ApiService
import com.dicoding.storyapp.data.DataStoreManager
import com.dicoding.storyapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private val apiService: ApiService by lazy {
        ApiClient.createApiClient(this)
    }
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val logoImageView: ImageView = findViewById(R.id.logo)

        startBounceAnimation(logoImageView)

        emailEditText = findViewById(R.id.ed_login_email)
        passwordEditText = findViewById(R.id.ed_login_password)
        loginButton = findViewById(R.id.btn_login)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val showPasswordCheckBox = findViewById<CheckBox>(R.id.checkbox_show_password)

        val dataStoreManager = DataStoreManager(applicationContext)

        val registerButton: Button = findViewById(R.id.btn_register_page)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val maxLength = 100
        emailEditText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))

        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmailFormat(emailEditText.text.toString())
            }
        }

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            passwordEditText.setSelection(passwordEditText.text.length)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (password.length >= 8) {
                loadingProgressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = apiService.login(email, password)

                        if (!response.error!! && response.loginResult != null) {
                            val token = response.loginResult.token ?: ""
                            dataStoreManager.saveToken(token)

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                            loadingProgressBar.visibility = View.INVISIBLE
                        } else {
                            Toast.makeText(this@LoginActivity, response.message ?: "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                            loadingProgressBar.visibility = View.INVISIBLE
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@LoginActivity, "Login failed. Please try again.", Toast.LENGTH_SHORT).show()
                        loadingProgressBar.visibility = View.INVISIBLE
                    }
                }
            } else {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateEmailFormat(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (email.matches(emailPattern.toRegex())) {
            emailEditText.error = null
            true
        } else {
            emailEditText.error = "Invalid email format"
            false
        }
    }

    private fun startBounceAnimation(view: View) {
        val translateYAnimator = ObjectAnimator.ofFloat(view, "translationY", -70f, 70f)
        translateYAnimator.repeatMode = ValueAnimator.REVERSE
        translateYAnimator.repeatCount = ObjectAnimator.INFINITE

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translateYAnimator)
        animatorSet.duration = 1000

        animatorSet.start()
    }
}

