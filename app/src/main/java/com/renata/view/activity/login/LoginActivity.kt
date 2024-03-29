package com.renata.view.activity.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.renata.R
import com.renata.data.Result
import com.renata.data.user.login.LoginPreferences
import com.renata.data.user.login.LoginResponse
import com.renata.data.user.login.LoginResult
import com.renata.databinding.ActivityLoginBinding
import com.renata.utils.AlarmReceiver
import com.renata.utils.ViewModelFactory
import com.renata.utils.emailValidation
import com.renata.utils.passwordValidation
import com.renata.view.activity.forgotpass.ForgotPassActivity
import com.renata.view.activity.main.NavigationActivity
import com.renata.view.activity.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        alarmReceiver = AlarmReceiver()
        loginViewModel = obtainViewModel(this as AppCompatActivity)
        showLoading(false)
        setupView()
        setupAnimation()
        emailET()
        passwordET()
        loginBinding.forgotPassword.setOnClickListener { forgotPassET() }
        loginBinding.createAccount.setOnClickListener { registerET() }
        loginBinding.loginButton.setOnClickListener {
            alarmReceiver.firstRepeatingAlarm(this)
            loginButton()
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): LoginViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[LoginViewModel::class.java]
    }

    private fun forgotPassET() {
        val moveToForgotPass = Intent(this, ForgotPassActivity::class.java)
        startActivity(moveToForgotPass)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        loginBinding.edLoginEmail.text = null
        loginBinding.edLoginPassword.text = null
    }

    private fun emailET() {
        val myLoginEmailET = loginBinding.edLoginEmail
        myLoginEmailET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val email = loginBinding.edLoginEmail.text.toString()
                if (email.isEmpty()) {
                    loginBinding.errorEmail.visibility = View.GONE
                } else {
                    if (emailValidation(email)) {
                        loginBinding.errorEmail.visibility = View.GONE
                    } else {
                        loginBinding.errorEmail.visibility = View.VISIBLE
                        loginBinding.errorEmail.text = getString(R.string.error_email)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun passwordET() {
        val myLoginPasswordET = loginBinding.edLoginPassword
        myLoginPasswordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val pass = loginBinding.edLoginPassword.text.toString()
                if (pass.isEmpty()) {
                    loginBinding.errorPass.visibility = View.GONE
                } else {
                    if (passwordValidation(pass)) {
                        loginBinding.errorPass.visibility = View.GONE
                    } else {
                        loginBinding.errorPass.visibility = View.VISIBLE
                        loginBinding.errorPass.text = getString(R.string.error_password)
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
        loginBinding.errorPass.visibility = View.GONE
        loginBinding.errorEmail.visibility = View.GONE
    }

    private fun setupAnimation() {
        val title =
            ObjectAnimator.ofFloat(loginBinding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val title2 =
            ObjectAnimator.ofFloat(loginBinding.titleTextView2, View.ALPHA, 1f).setDuration(500)
        val email =
            ObjectAnimator.ofFloat(loginBinding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailInput = ObjectAnimator.ofFloat(loginBinding.emailEditTextLayout, View.ALPHA, 1f)
            .setDuration(500)
        val password =
            ObjectAnimator.ofFloat(loginBinding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordInput =
            ObjectAnimator.ofFloat(loginBinding.passwordEditTextLayout, View.ALPHA, 1f)
                .setDuration(500)
        val forgotPass =
            ObjectAnimator.ofFloat(loginBinding.forgotPassword, View.ALPHA, 1f)
                .setDuration(500)
        val login =
            ObjectAnimator.ofFloat(loginBinding.loginButton, View.ALPHA, 1f).setDuration(500)
        val create =
            ObjectAnimator.ofFloat(loginBinding.goToRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                title2,
                email,
                emailInput,
                password,
                passwordInput,
                forgotPass,
                login,
                create
            )
            start()
        }
    }

    private fun registerET() {
        val moveToRegister = Intent(this, RegisterActivity::class.java)
        startActivity(moveToRegister)
        overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom)
        loginBinding.edLoginEmail.text = null
        loginBinding.edLoginPassword.text = null
    }

    private fun loginButton() {
        val email = loginBinding.edLoginEmail.text.toString()
        val password = loginBinding.edLoginPassword.text.toString()
        when {
            email.isEmpty() && password.isEmpty() -> {
                showLoading(false)
                insertEmail()
                insertPass()
            }

            email.isEmpty() -> {
                showLoading(false)
                insertEmail()
            }

            password.isEmpty() -> {
                showLoading(false)
                insertPass()
            }

            else -> {
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    if (passwordValidation(password) && emailValidation(email)) {
                        showLoading(true)
                        login(email, password)
                    } else {
                        showLoading(false)
                        showAlert(
                            getString(R.string.login_fail),
                            getString(R.string.login_fail_cause1)
                        ) {}
                    }
                } else {
                    showLoading(false)
                    showAlert(
                        getString(R.string.login_fail),
                        getString(R.string.login_fail_cause2)
                    ) { finish() }
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        loginViewModel.userLogin(email, password).observe(this@LoginActivity) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Error -> {
                        showLoading(false)
                        val errorMessage = result.data
                        showAlert(
                            getString(R.string.login_fail),
                            errorMessage
                        ) {}
                    }

                    is Result.Success -> {
                        showLoading(false)
                        loginSuccess(result.data)
                    }
                }
            }
        }
    }

    private fun loginSuccess(loginResponse: LoginResponse) {
        saveLoginData(loginResponse)
        val moveToMain = Intent(this, NavigationActivity::class.java)
        startActivity(moveToMain)
        overridePendingTransition(
            R.anim.slide_out_bottom,
            R.anim.slide_in_bottom
        )
    }

    private fun saveLoginData(loginResponse: LoginResponse) {
        val loginPreference = LoginPreferences(this)
        val loginResult = loginResponse.data
        val loginModel = LoginResult(
            id = loginResult.id, email = loginResult.email, token = loginResult.token
        )
        loginPreference.setLogin(loginModel)
    }

    private fun insertEmail() {
        loginBinding.errorEmail.visibility = View.VISIBLE
        loginBinding.errorEmail.text = getString(R.string.insert_email)
    }

    private fun insertPass() {
        loginBinding.errorPass.visibility = View.VISIBLE
        loginBinding.errorPass.text = getString(R.string.insert_pass)
    }

    private fun showAlert(
        title: String,
        message: String,
        positiveAction: (dialog: DialogInterface) -> Unit
    ) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> positiveAction.invoke(dialog) }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loginBinding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}