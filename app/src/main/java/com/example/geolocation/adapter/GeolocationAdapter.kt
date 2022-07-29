package com.example.geolocation.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocation.R
import com.example.geolocation.db.GeolocationDatabase
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
        holder.itemView.item_latitude_value.text = listGeolocation[position].latitude
        holder.itemView.item_longitude_value.text = listGeolocation[position].longitude
        holder.itemView.button_delete.setOnClickListener{
                val builder = AlertDialog.Builder(it.context)
                builder.setTitle("Удаление данных")
                builder.setMessage("Вы действительно хотите удалить данные?")
                builder.setNegativeButton("Отмена"){ dialog, _ ->
                    dialog.cancel()
                }
                builder.setPositiveButton("Удалить"){ _, _ ->
                    val id = listGeolocation[position].id
                    GeolocationDatabase.getInstance(Application()).getGeolocationDao().deleteById(id)
                }
                builder.show()

        }

        holder.itemView.button_update.setOnClickListener {
            val id = listGeolocation[position].id

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Изменение название точки")
            builder.setMessage("Введите ваше название")
            val editTextDialog = EditText(it.context)
            builder.setView(editTextDialog)
            builder.setNegativeButton("Отмена"){ dialog, _ ->
                dialog.cancel()
            }
            builder.setPositiveButton("Подтвердить") { _, _ ->
                val title = if (editTextDialog.text.toString() == "") {
                    "New Point"
                } else {
                    editTextDialog.text.toString()
                }
                GeolocationDatabase.getInstance(Application()).getGeolocationDao()
                    .updateById(id, title)
            }
            builder.show()
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