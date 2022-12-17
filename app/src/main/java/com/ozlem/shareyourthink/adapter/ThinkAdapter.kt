package com.ozlem.shareyourthink.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ozlem.shareyourthink.R
import com.ozlem.shareyourthink.model.Sharing
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_row.view.*

// Önce class'ın bir recyclerView adapter olduğunu söyledik.
// Bu da bizden parametre olarak bir viewholder istiyor.
// SharingHolder aslında bir sınıf olmalı. Bu ViewHolder sınıfını adapter'ın içinde oluşturuyoruz.
// sharingList bir ArrayList ve içinde Sharing'ler olacak.
class ThinkAdapter (val sharingList : ArrayList<Sharing>) : RecyclerView.Adapter<ThinkAdapter.SharingHolder>(){

    // RecyclerView.ViewHolder sınıfı bizden bir view istiyor.
    // Bu view'uda constructor'ın içinde alalım (itemview)
    class SharingHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    }

    // recycler_row.xml ile RecyclerView'u birbirine bağlayan fonksiyondur:
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharingHolder {
        // ViewHolder oluşturulduğunda ne yapılacağını bu metodun içine yazıyoruz.
        // Bir xml ile buradaki kodu bağlarken inflater kullanıyorduk.
        // from içinde context'i vermemiz gerekiyor.
        // parent bize hangi ana grup içinde bulunduğumuzu gösteriyor. Ve parent zaten bu metodda veriliyor.
        // parent.context diyerek içinde bulunduğumuz context'i alabiliyoruz.
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        // Bizden bir viewHolder döndürmemiz isteniyor.
        return SharingHolder(view)
    }

    override fun onBindViewHolder(holder: SharingHolder, position: Int) {
        holder.itemView.recycler_row_username.text = sharingList[position].username
        holder.itemView.recycler_row_sharing_message.text = sharingList[position].sharedComment

        // İlgili pozisyondaki paylaşımın imageUrl'i null değilse resim gösterilecek demektir.
        if(sharingList[position].imageUrl != null){
            // Görselin visibility'sini değiştirelim:
            holder.itemView.recycler_row_imageViewID.visibility = View.VISIBLE
            // Picasso ile resimi yüklüyoruz:
            // load kısmı nereden yükleyeceği
            // into kısmı ise nereye yükleyeceği.
            Picasso.get().load(sharingList[position].imageUrl).into(holder.itemView.recycler_row_imageViewID)
        }

    }

    override fun getItemCount(): Int {
        // Kaç tane paylaşım varsa RecyclerView'da o kadar satır olacak:
        return sharingList.size
    }

}