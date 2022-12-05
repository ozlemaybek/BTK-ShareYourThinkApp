package com.ozlem.shareyourthink.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.ozlem.shareyourthink.R
// xml-activty bağlantısı için:
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    // Bir FirebaseAuth objesi oluşturalım:
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Oluşturduğumuz FirebaseAuth objesini initialize edelim:
        auth = Firebase.auth

        // Giriş yapmış kullanıcıyı hatırlamak:
        // currentUser bize nullable olarak veriliyor yani oladabilir, olmayadabilir.
        // Eğer null ise yok demektir yani firebase kullanıcının giriş yaptığını anlamamış demektir.
        val currentUser = auth.currentUser

        // Kullanıcı daha önce uygulamaya giriş yaptıysa ve uygulamayı şuan açarsa log-in ekranı gösterilmeden
        // direkt diğer ekrana geçilecek.
        if(currentUser != null){
            val intent = Intent(this, ThinkActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Peki ya kullanıcı çıkış yapıp başka bir hesapla giriş yapmak isterse
    }

    fun signInOnclick(view: View){
        // Kullanıcının girdiği email'i bir string olarak alalım:
        val email = emailTextID.text.toString()
        val password = passwordTextID.text.toString()

        if(email != "" && password != ""){
            // Kayıtlı birkullanıcının sign in olması:
            auth.signInWithEmailAndPassword(email, password) .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Kullanıcı şu anda girdi. (current user: güncel kullanıcı)
                    val currentUser = auth.currentUser?.displayName.toString()
                    Toast.makeText(applicationContext,"Welcome ${currentUser}", Toast.LENGTH_LONG).show()

                    // sign in işlemi başarılı ise diğer ekrana geçelim:
                    val intent = Intent(this, ThinkActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }.addOnFailureListener{ exception ->
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun signUpOnclick(view: View){

        // Kullanıcının girdiği email'i bir string olarak alalım:
        val email = emailTextID.text.toString()
        val password = passwordTextID.text.toString()
        val username = usernameTextID.text.toString()

        // Kullanıcımızı oluşturalım:
        // Kullanıcıyı oluşturduğumuz satırın hemen devamında bir listener var ve ne olacağını kontrol ediyor.
        // onCompleteListener'ın değişken adı "task" olarak belirlenmiş. Başka bir şeyde kullanabilirdik.
        // task : authentication'ın sonucu
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // if içnde isSuccessful ile task başarılımı bunu kontrol ediyoruz.
                    Toast.makeText(applicationContext, "Sign Up was successful.", Toast.LENGTH_LONG).show()

                    // USERNAME'İ GÜNCELLEMEK:
                    // Önce güncel kullanıcıyı alalım:
                    val currentUser = auth.currentUser
                    // Şimdi güncel kullanıcı ile ilgili yapılacak request'i oluşturalım:
                    // userProfileChangeRequest: firebase'de hazır tanımlı bir fonksiyon.
                    val profileUpdatesRequest = userProfileChangeRequest {
                        displayName = username
                    }

                    // Şimdi yukarıda oluşturduğumuz profil güncelleme isteğini alıp aşağıda kullanabiliriz.
                    if(currentUser != null){
                        currentUser.updateProfile(profileUpdatesRequest).addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                Toast.makeText(applicationContext, "Profile username added.", Toast.LENGTH_LONG).show()
                            }

                        }
                    }


                    // Kayıt olma işlemi başarılı ise kullanıcı ThinkActivity ekranına aktarılacak:
                    // 1.parametre: context(bulunduğum yer)
                    // 2.parametre: gideceğim yer
                    val intent = Intent(this, ThinkActivity::class.java)
                    startActivity(intent) // 2.ekrana geçişi başlat
                    finish() // bulunduğum aktiviteyi kapatıp hafızadan sil.
                }
            }.addOnFailureListener{ exception ->
                // addOnFailureListener bize bir exception yani hata veriyor.
                // Aldığımız exception'ı gösterelim:
                // 1.parametre: context
                // 2.parametre: gösterilecek mesaj metni
                // 3.parametre: duration
                // exception.localizedMessage kısmı ile hata mesajlarının kullanıcının anlayacağı bir dil ile görünmesini sağladık.
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }




}