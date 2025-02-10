package com.example.gr2sw2024b_cara

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.IdpResponse
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class HFirebaseUiAuth : AppCompatActivity() {
    private val respuestaLoginUiAuth = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ){
            res: FirebaseAuthUIAuthenticationResult ->
        if(res.resultCode == RESULT_OK){
            if(res.idpResponse != null){
                seLogeo(res.idpResponse!!)
            }
        }
    }

    fun seLogeo(res:IdpResponse){
        val nombre = FirebaseAuth.getInstance().currentUser?.displayName
        cambiarInterfaz(View.INVISIBLE, View.VISIBLE, nombre!!)
        if(res.isNewUser == true){
            registrarUsuarioPorPrimeraVez(res)
        }
    }

    fun cambiarInterfaz(
        visibilidadLogin:Int = View.VISIBLE,
        visibilidadLogout: Int = View.INVISIBLE,
        textoTextView:String = "Bienvenido"){
        val btnLogin = findViewById<Button>(R.id.btn_login_firebase)
        val btnLogout = findViewById<Button>(R.id.btn_logout_firebase)
        val tvBienvenida = findViewById<TextView>(R.id.tv_bienvenido)
        btnLogin.visibility = visibilidadLogin
        btnLogout.visibility = visibilidadLogout
        tvBienvenida.text = textoTextView
    }

    // Registramos en nuestro sistema y ej enviamos correo etc etc
    fun registrarUsuarioPorPrimeraVez(usuario:IdpResponse){}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hfirebase_ui_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnLogin = findViewById<Button>(R.id.btn_login_firebase)
        btnLogin.setOnClickListener {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )
            val logearseIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build()
            respuestaLoginUiAuth.launch(logearseIntent)
        }
        val btnLogout = findViewById<Button>(R.id.btn_logout_firebase)
        btnLogout.setOnClickListener {
            cambiarInterfaz()
            FirebaseAuth.getInstance().signOut()
        }
        val usuario = FirebaseAuth.getInstance().currentUser
        if(usuario !=null){
            cambiarInterfaz(View.INVISIBLE, View.VISIBLE, usuario.displayName!!)
        }
    }


}