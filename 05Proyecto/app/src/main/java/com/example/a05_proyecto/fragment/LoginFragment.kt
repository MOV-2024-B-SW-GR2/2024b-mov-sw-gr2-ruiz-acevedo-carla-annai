package com.example.a05_proyecto.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.a05_proyecto.R
import com.example.a05_proyecto.activity.DashboardActivity
import com.example.a05_proyecto.utils.ConnectionManager
import org.json.JSONException
import org.json.JSONObject


class LoginFragment(val contextParam: Context) : Fragment() {
    lateinit var txtSignUp: TextView
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var btnLogin: Button
    lateinit var loginProgressDialog: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        etMobileNumber = view.findViewById(R.id.etMobileNumber)
        etPassword = view.findViewById(R.id.etPassword)
        txtForgotPassword = view.findViewById(R.id.txtForgotPassword)
        txtSignUp = view.findViewById(R.id.txtSignUp)
        btnLogin = view.findViewById(R.id.btnLogin)
        loginProgressDialog = view.findViewById(R.id.loginProgressDialog)

        loginProgressDialog.visibility = View.GONE

        //under line text
        txtForgotPassword.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        txtSignUp.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        txtForgotPassword.setOnClickListener {
            openForgotPasswordInputFragment()
        }

        txtSignUp.setOnClickListener {
            openRegisterFragment()
        }

        btnLogin.setOnClickListener {

            btnLogin.visibility = View.GONE

            if (etMobileNumber.text.isBlank()) {
                etMobileNumber.setError("Mobile Number Missing")
                btnLogin.visibility = View.VISIBLE
            } else {
                if (etPassword.text.isBlank()) {
                    btnLogin.visibility = View.VISIBLE
                    etPassword.setError("Missing Password")
                } else {
                    loginUserFun()
                }
            }
        }
        return view
    }

    fun openForgotPasswordInputFragment() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.frameLayout,
            ForgotPasswordInputFragment(contextParam)
        )
        transaction?.commit()//apply changes
    }


    fun openRegisterFragment() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.frameLayout,
            RegisterFragment(contextParam)
        )
        transaction?.commit()
    }


    fun loginUserFun() {
        val sharedPreferencess = contextParam.getSharedPreferences(
            getString(R.string.shared_preferences),
            Context.MODE_PRIVATE
        )

        // Datos de usuarios "quemados"
        val users = mapOf(
            "0987965903" to "1234",  // Número de móvil y su contraseña
            "0987654321" to "mypassword"    // Otro usuario de prueba
        )

        // Validar la conexión a internet
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            loginProgressDialog.visibility = View.VISIBLE

            // Validar que se haya ingresado el número y la contraseña
            if (etMobileNumber.text.isBlank()) {
                etMobileNumber.setError("Mobile Number Missing")
                loginProgressDialog.visibility = View.GONE
            } else if (etPassword.text.isBlank()) {
                etPassword.setError("Missing Password")
                loginProgressDialog.visibility = View.GONE
            } else {
                // Comprobar si el usuario existe en los "datos quemados"
                val mobileNumber = etMobileNumber.text.toString()
                val password = etPassword.text.toString()

                if (users.containsKey(mobileNumber) && users[mobileNumber] == password) {
                    // Datos de usuario válidos, guardar en SharedPreferences
                    sharedPreferencess.edit().putBoolean("user_logged_in", true).apply()
                    sharedPreferencess.edit().putString("user_id", "12345").apply()
                    sharedPreferencess.edit().putString("name", "Charlie").apply()
                    sharedPreferencess.edit().putString("email", "c@hotmail.com").apply()
                    sharedPreferencess.edit().putString("mobile_number", mobileNumber).apply()
                    sharedPreferencess.edit().putString("address", "Quito").apply()

                    // Mostrar mensaje de bienvenida
                    Toast.makeText(contextParam, "Welcome Charlie", Toast.LENGTH_LONG).show()

                    // Llamar a la función que simula el login exitoso
                    userSuccessfullyLoggedIn()
                } else {
                    // Si el número o la contraseña no coinciden
                    Toast.makeText(contextParam, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    loginProgressDialog.visibility = View.GONE
                }
            }
        } else {
            loginProgressDialog.visibility = View.GONE

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.create()
            alterDialog.show()
        }
    }

    fun userSuccessfullyLoggedIn() {
        val intent = Intent(activity as Context, DashboardActivity::class.java)
        startActivity(intent)
        activity?.finish();
    }

    override fun onResume() {
        if (!ConnectionManager().checkConnectivity(activity as Context)) {
            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be established!")
            alterDialog.setPositiveButton("Open Settings")
            { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit")
            { _, _ ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
        super.onResume()
    }
}