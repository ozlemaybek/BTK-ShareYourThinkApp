package com.ozlem.shareyourthink

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ThinkActivity : AppCompatActivity() {

    // Bir FirebaseAuth objesi oluşturalım:
    private lateinit var auth: FirebaseAuth

    // Menü resource'unu bağlayacağımız fonksiyon:
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // xml ve activity kodlarını bağlamak için inflater kullanıyorduk.
        // menuInflater'ı kullanarak oluşturduğumuz main_menu.xml'i buraya bağladık.
        val manuInflater = menuInflater
        // 1.parametre: kaynak menu xml'i
        // 2.parametre: onCreateOptionsMenu fonksiyonunda (içinde olduğumuz fonksiyon) alınan menu parametresi
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Menüden herhangi bir seçenek seçildiğinde ne yapacağımızı belirttiğimiz fonksiyon:
    // Biz sadece "Log Out" isimli tek bir options koyduk fakat birden fazla olabilirdi
    // ve her birinde ne yapılacağını burada belirtirdik.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Seçilen option bize bu fonksiyonun parametresi olarak veriliyor:
        if(item.itemId == R.id.log_out_id){
            // Güncel kullanıcının sign out işlemini yapar ve cache'i temizler.
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_think)

        // Oluşturduğumuz FirebaseAuth objesini initialize edelim:
        auth = Firebase.auth
    }
}