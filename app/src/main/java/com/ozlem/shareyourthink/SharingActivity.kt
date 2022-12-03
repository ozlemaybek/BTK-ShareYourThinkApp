package com.ozlem.shareyourthink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sharing.*

class SharingActivity : AppCompatActivity() {

    // Bir firestore objesi oluşturalım:
    val db = Firebase.firestore
    // Authentication objesi oluşturalım:
    // auth isimli objenin bir FirebaseAuth objesi olacağını söyledik:
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing)
        auth = Firebase.auth
    }

    fun shareButtonOnClick(view : View){
        // Kullanıcının paylaşmak istediği yorumu alalım:
        val sharedComment = sharingTextID.text.toString()
        val username = auth.currentUser!!.displayName.toString()
        // tarihi almak için firebase'den gelen Timestamp sınıfını kullanalım:
        // Şuandaki tarihi alır:
        val date = Timestamp.now()

        // Database'e ekleyeceğimiz şeyleri bir hashmap içine koyarak ekleyeceğiz.
        // 1.parametre: anahtar kelime (key) - field name. Anahtar kelimelerim hep string olacak çünkü field name'ler string olmak zorunda.
        // 2.parametre: value. (Değer herhangi bir şey olabileceği için Any dedik, Çünkü farklı farklı veri türlerini kaydedebiliriz.)
        val sharedCommentMap = hashMapOf<String, Any>()
        // Oluşturduğumuz Map içine eklemelerimizi yapalım:
        // 1.parametre: key
        // 2.parametre: value
        sharedCommentMap.put("sharedComment", sharedComment)
        sharedCommentMap.put("username", username)
        sharedCommentMap.put("date", date)

        // Şimdi yukarıda aldığımız 3 bilgiyi veritabanımıza kaydedelim.
        // collectionPath: collection'ımızın isminin ne olmasını istiyorsak o.
        db.collection("Shares").add(sharedCommentMap).addOnCompleteListener{ task ->
            if(task.isSuccessful) {
                // İşlem başarılı ise bu activity'yi sonlandır ve ThinkActivity.kt'ye geri dön:
                finish()
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}