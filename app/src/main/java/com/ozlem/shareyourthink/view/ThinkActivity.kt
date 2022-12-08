package com.ozlem.shareyourthink.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ozlem.shareyourthink.R
import com.ozlem.shareyourthink.adapter.ThinkAdapter
import com.ozlem.shareyourthink.model.Sharing
import kotlinx.android.synthetic.main.activity_think.*

class ThinkActivity : AppCompatActivity() {

    // Bir FirebaseAuth objesi oluşturalım:
    private lateinit var auth: FirebaseAuth
    // Database objemizi oluşturalım:
    val db = Firebase.firestore
    // sharingLsit bir arraylist olacak ve içinde Sharing objelerini tutacak:
    var sharingList = ArrayList<Sharing>()
    // RecyclerView Adapter'ımızı tanımlayalım:
    // recyclerViewAdapter bir ThinkAdapter olacak:
    private lateinit var recyclerViewAdapter : ThinkAdapter

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
        }else if(item.itemId == R.id.do_share_id){
            val intent = Intent(this, SharingActivity::class.java)
            startActivity(intent)
            // finish() demedik çünkü kullanıcı geri dönmek isteyebilir.
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_think)

        // Oluşturduğumuz FirebaseAuth objesini initialize edelim:
        auth = Firebase.auth

        firebase_get_data()

        // Bir layout manager oluşturduk.
        // Bu şunu sağlıyor: RecylerView'da elemanlar alt alta mı gösterilsin grid layout olarakmı gösterilsin bunu seçiyoruz.
        // Biz alt alta göstereceğiz.
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        // recyclerViewAdapter'ı initialize edelim:
        recyclerViewAdapter = ThinkAdapter(sharingList)
        recyclerView.adapter = recyclerViewAdapter
    }
    fun firebase_get_data(){
        // Database'den veri çekelim:
        db.collection("Shares").addSnapshotListener{snapshot, error ->
            if(error != null){
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            }else{
                // Hata mesajı yoksa büyük ihtimalle snapshot'ımız gelmiştir.
                // Fakat snapshot bize ? yani nullable olarak geliyor. Bu yüzden if ekleyelim:
                if(snapshot != null){
                    // Burada snapshot'ım null olmayabilir ama içinde bir doküman olmayabilir.
                    // isEmpty() ile gittiğimiz cllection'ın içinde bir document var mı yok mu öğrenebiliriz.
                    // Kontrol ediyoruz çünkü içinde document olmayan bir collection'ada gitmiş olabiliriz.
                    // Eğer boşsa true döner:
                    if(!snapshot.isEmpty){
                        // Hem snapshot null değil, hem hata mesajı yok hem de içinde document var:
                        // Aşağıdaki documents değişkeni collection içindeki tüm document'ları barındıran bir dizi:
                        val documents = snapshot.documents
                        // for loop'a girmeden önce temizledik. Eğer temizlemeseydik her bir paylaşım olduğunda üstüne yazacaktı
                        // ve bir sürü paylaşım gözükecekti:
                        sharingList.clear()
                        for (document in documents){
                            // Bu for loop'un içinde document'lara tek tek ulaşalım.
                            // Any geliyordu String'e çevirmek için as String dedik:
                            val username = document.get("username") as String
                            val sharedComment = document.get("sharedComment") as String
                            val imageUrl = document.get("imageUrl") as String?
                            val downloadedShare = Sharing(username, sharedComment, imageUrl)
                            sharingList.add(downloadedShare)
                        }
                        // Yeni veri geldi haberin olsun diyoruz böylece recylerView verileri göstermeye çalışacak:
                        recyclerViewAdapter.notifyDataSetChanged()
                    }

                }
            }
        }
    }
}