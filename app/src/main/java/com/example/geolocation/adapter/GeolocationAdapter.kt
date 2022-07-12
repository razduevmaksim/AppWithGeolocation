package com.example.geolocation.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.geolocation.R
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.db.dao.GeolocationDao
import com.example.geolocation.model.GeolocationModel
import kotlinx.android.synthetic.main.item_layout.view.*

class GeolocationAdapter : RecyclerView.Adapter<GeolocationAdapter.GeolocationViewHolder>() {

    private var listGeolocation = emptyList<GeolocationModel>()

    class GeolocationViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeolocationViewHolder {
        //указание на item_layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return  GeolocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeolocationViewHolder, position: Int) {
        holder.itemView.item_title.text = listGeolocation[position].title
        holder.itemView.item_lattitude_value.text = listGeolocation[position].latitude
        holder.itemView.item_longitude_value.text = listGeolocation[position].longitude
        holder.itemView.button_delete.setOnClickListener{
            var id = listGeolocation[position].id
            val title = listGeolocation[position].title
            val latitude = listGeolocation[position].latitude
            val longitude = listGeolocation[position].longitude
            holder.itemView.item_title.id
            GeolocationDatabase.getInstance(Application()).getGeolocationDao().deleteById(id)


        }
    }

    override fun getItemCount(): Int {
        //Количество возвращаемых значений
        return listGeolocation.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: List<GeolocationModel>){
        listGeolocation = list
        //обновление при изменении данных
        notifyDataSetChanged()
    }
}