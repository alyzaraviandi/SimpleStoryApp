package com.dicoding.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
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
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.remote.retrofit.ApiClient
import com.dicoding.storyapp.remote.retrofit.ApiService
import com.dicoding.storyapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private val apiService: ApiService by lazy {
        ApiClient.createApiClient(this)
    }
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.ed_register_name)
        emailEditText = findViewById(R.id.ed_register_email)
        passwordEditText = findViewById(R.id.ed_register_password)
        registerButton = findViewById(R.id.btn_register)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        val logoImageView: ImageView = findViewById(R.id.logo)
        startBounceAnimation(logoImageView)

        val showPasswordCheckBox = findViewById<CheckBox>(R.id.checkbox_show_password)

        val loginButton: Button = findViewById(R.id.btn_login_page)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
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

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (password.length >= 8) {
                loadingProgressBar.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val response = apiService.register(name, email, password)

                        if (!response.error!!) {
                            Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                            loadingProgressBar.visibility = View.INVISIBLE
                        } else {
                            Toast.makeText(this@RegisterActivity, response.message, Toast.LENGTH_SHORT).show()
                            loadingProgressBar.visibility = View.INVISIBLE
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@RegisterActivity, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
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

