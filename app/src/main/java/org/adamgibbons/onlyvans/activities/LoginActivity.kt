package org.adamgibbons.onlyvans.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.adamgibbons.onlyvans.MainApp
import org.adamgibbons.onlyvans.databinding.ActivityLoginBinding
import org.adamgibbons.onlyvans.helpers.AuthHelpers
import org.adamgibbons.onlyvans.models.UserModel

class LoginActivity : AppCompatActivity() {

    private lateinit var app: MainApp
    private lateinit var binding: ActivityLoginBinding
    private val user = UserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        app = application as MainApp
        if (app.prefs.userLoggedIn) {
            startVanListActivity()
        } else {
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }
    }

    fun handleLogin(view: View) {

        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {

            val user = app.users.findByUsername(username)
            if (user != null && app.users.verifyPassword(user, password)) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_LONG).show()
                app.prefs.userLoggedIn = true
                startVanListActivity()
            } else {
                Toast.makeText(this, "No user found...", Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(this, "Username/Password cannot be empty.", Toast.LENGTH_LONG).show()
        }
    }

    private fun startVanListActivity() {
        val myIntent = Intent(this, VanListActivity::class.java)
        startActivity(myIntent)
    }

    fun createAccount(view: View) {

        val username = binding.username.text.toString()
        val password = binding.password.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            user.username = username
            user.password = password
            app.users.create(user)
            Toast.makeText(this, "Account created!", Toast.LENGTH_LONG).show()
            app.prefs.userLoggedIn = true
            startVanListActivity()
        } else {
            Toast.makeText(this, "Username/Password cannot be empty.", Toast.LENGTH_LONG).show()
        }
    }

}