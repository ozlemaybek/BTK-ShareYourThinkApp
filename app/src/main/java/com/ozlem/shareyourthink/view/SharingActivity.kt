package com.ozlem.shareyourthink.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.ozlem.shareyourthink.R
import kotlinx.android.synthetic.main.activity_sharing.*
import java.util.UUID

class SharingActivity : AppCompatActivity() {

    // Bir firestore objesi oluşturalım:
    val db = Firebase.firestore
    // Authentication objesi oluşturalım:
    // auth isimli objenin bir FirebaseAuth objesi olacağını söyledik:
    private lateinit var auth : FirebaseAuth
    // onActivityResult metodu için:
    // choosenImage ilk başta bir Uri nullable olacak ve ilk değeri null olacak.
    var choosenImage : Uri? = null
    // bitmap android'de görselleri tuttuğumuz sınıftır.
    var choosenBitmap : Bitmap? = null
    // Bir storage objesi oluşturalım:
    val storage = Firebase.storage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharing)
        auth = Firebase.auth
    }

    fun shareButtonOnClick(view : View){

        if(choosenImage != null){
            // Bir referans oluşturalım:
            // reference değişkeni bizim depomuzun kendisine referans veriyor.
            val reference = storage.reference

            // Her defasında random oluşturulacak unique bir id oluşturmak istiyoruz:
            // Java'da bulunan UUID sınıfını kullanalım.
            val uuid = UUID.randomUUID()
            val imageName = "${uuid}.jpg"

            // Storage modülünde images klasörünü zaten oluşturduk fakat oluşturmamıza gerek yoktu.
            // Aşağıdaki kod satırını yazdığımızda images klasörü otomatik olarak depo içerisinde oluşturulacaktır.
            val imageReference = reference.child("images").child("imageName")

            // choosenImage nullable olduğu için hata verdi !! ile düzelttik:
            imageReference.putFile(choosenImage!!).addOnSuccessListener { task ->
                // Yüklendikten sonra url'i almalıyız.
                // URL ALINACAK:
                val uploadedImageReference = reference.child("images").child("imageName")
                uploadedImageReference.downloadUrl.addOnSuccessListener{ uri ->
                    // downloadUrl içinde resmin Url'i tutuluyor:
                    val downloadUrl = uri.toString()
                    println(downloadUrl)

                        // comment içi başladı:

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
                    sharedCommentMap.put("imageUrl",downloadUrl)

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

                    // comment içi bitti
                }
            }.addOnFailureListener { exception ->
                // Burada exception fırlatılırsa onu kontrol edelim:
                Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }

    fun addImageButtonOnClick(view : View){
        // Butona tıklandığında önce kullanıcıdan izin istemeliyiz:
        // if içindeki Manifest'i android olan seçmeye dikkat etmeliyiz.
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            // Bu if'in içine girildiyse izin verilmemiş ve izin istememiz gerekiyor demektir.
            // requestPermissions() : izinleri iste demektir.
            // Hangi izinleri isteyeceğimizi bir dizi olarak vermemiz gerekiyor.
            // Tek izin isteyeceğiz ama yinede dizi olarak vermemiz gerekiyor.
            // Son parametrede bizeden integer bir istek kodu istiyor. requestCode yapılan izin isteğinin kodu.
            // Bu kodu kullanarak sonrasında bu izinden mi bahsediyoruz kontrol edebileceğiz.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }else{
            // İzin zaten verilmiş direkt galeriye gidebiliriz:
            // Galeriye gidelim:
            // PICK : almak demektir.
            // 3.parametre: veriyi nereden alacağım.
            // galleryIntent içinde galeride kayıtlı resmin konumunu tutacak:
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // Daha önce startActivity kullanıyorduk fakat şimdi startActivityForResult() kullandık.
            // ForResult sonucunda bir cevap dönecek ve ben bu cevabı işleyeceğim demek:
            startActivityForResult(galleryIntent,2)

        }
    }

    // İzin verilirse ne yapacağız:
    // Aşağıdaki fonksiyon izinlerin sonucunda çağrılacak fonksiyondur.
    // Bu fonksiyonun içinde bize bir requestCode, izinler ve grantResults yani bana geri dönülen cevaplar veriliyor.
    // grantResults: bana gelen cevaplar. Eğer bana bir cevap gelmediyse veya izin verilmediyse bir şey yapmama gerek yok.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Aynı activity ya da fragment içinde birden fazla izin isteği olabilir bu yüzden requestCode'u kontrol edelim:
        if(requestCode == 1){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Bu kod bloğunun içine girildiyse izin verildiği kesinleşmiştir.
                // Aynı şey ile ilgili requestCode'un aynı olması önemli yukarıda requestCode'a 2 verdiğimiz için burada da 2 verdik.
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // Daha önce galeri izni verildiyse ve direkt galeriye gidilip resim seçildiyse:
    // Aşağıdaki fonksiyon yapılan işlemin sonucunda bize ne döndürüldüğünü gösteriyor:
    // Bu fonksiyon bizden 3 parametre istiyor.
    // 1.parametre: requestCode (istek kodu)
    // 2.parametre: resultCode (sonuç kodu)
    // 3.parametre: data (geri döndürülen data)
    // RESULT_OK : result okey: kullanıcı düzgün bir cevap geri döndü demektir.
    // RESULT_CANCELLED : kullanıcı herhangi bir görsel seçmekten vazgeçti.
    // data != null : geri dönen data null'a eşit değil.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == RESULT_OK && data != null){
            // Eğer iif içindeki 3 şartta sağlanıyorsa kullanıcı bize düzgün bir cevap geri dönmüştür.
            // Şimdi geri dönen veriyi alalım:
            // data.data dediğimizde bu bize bir Uri verir.
            // Uri: seçilen görselin telefon içerisinde kaydedildiği konum. Bu konum ile işlemlerimizi yapabiliyoruz.
            // choosenImage içinde Uri'ı tutuyoruz:
            choosenImage = data.data
            // imageView'u xml tarafında görünmez yapmıştık şimdi görünür yapmalıyız:
            imageViewID.visibility = View.VISIBLE
            if(choosenImage != null){
                // Öncelikle Uri'ı bitmap'e çevirmemiz gerekecek fakat bu konuda API 28 ve sonrası için farklılıklar var.
                // Bu yüzden 28 ve sonrası için yapacaklarımızı if bloğunda 28 öncesi için yapacaklarımızı ise else bloğunda yazıyoruz:
                // Build.VERSION.SDK_INT = Android işletim sisteminin versiyonu
                if(Build.VERSION.SDK_INT >= 28){
                    // Önce bir source yani kaynak oluşturmamız gerekiyor.
                    // Bu işlemi android.graphics içindeki ImageDecoder sınıfını kullanarak yapmamız gerekiyor.
                    // createSource() diyerek bir source oluşturuyoruz.Bizden parametre olarak bir contentResolver birde Uri istiyor.
                    val source = ImageDecoder.createSource(this.contentResolver, choosenImage!!)
                    choosenBitmap = ImageDecoder.decodeBitmap(source)
                    imageViewID.setImageBitmap(choosenBitmap)
                } else{
                    // getBitmap() deprecated yani tedahülden kalkmış bir metod fakat API 28 öncesinde çalırken 28 ve sonrasında çalışmıyor.
                    // getBitmap bizden bir contentResolver yani içerik çözümleyici istiyor. Bunu this.contentResolver diyerek alabiliriz.
                    // 2. parametre olarakta çözümlenecek bir Uri veriyoruz:
                    choosenBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, choosenImage)
                    // Seçilen bitmap'i imageView içinde gösterelim:
                    imageViewID.setImageBitmap(choosenBitmap)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}