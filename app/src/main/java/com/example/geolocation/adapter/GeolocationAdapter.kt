package com.example.geolocation.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.geolocation.*
import com.example.geolocation.db.GeolocationDatabase
import com.example.geolocation.model.GeolocationModel
import kotlinx.android.synthetic.main.item_layout.view.*

class GeolocationAdapter : RecyclerView.Adapter<GeolocationAdapter.GeolocationViewHolder>() {
    private var listGeolocation = emptyList<GeolocationModel>()
    private lateinit var preferences: SharedPreferences

    class GeolocationViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeolocationViewHolder {
        //указание на item_layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return GeolocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: GeolocationViewHolder, position: Int) {
        //установка значений в TextView
        holder.itemView.item_title.text = listGeolocation[position].title
        holder.itemView.item_latitude_value.text = listGeolocation[position].latitude
        holder.itemView.item_longitude_value.text = listGeolocation[position].longitude

        //переход на ItemActivity при клике на item
        holder.itemView.setOnClickListener {
            val title = listGeolocation[position].title
            val latitude = listGeolocation[position].latitude
            val longitude = listGeolocation[position].longitude
            preferences =
                it.context.getSharedPreferences(GEOLOCATION_PREFERENCES_ITEM, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putString(GEOLOCATION_PREFERENCES_TITLE_ITEM, title)
            editor.putFloat(GEOLOCATION_PREFERENCES_LATITUDE_ITEM, latitude.toFloat())
            editor.putFloat(GEOLOCATION_PREFERENCES_LONGITUDE_ITEM, longitude.toFloat())
            editor.putBoolean(GEOLOCATION_PREFERENCES_VALIDATION_ITEM, true)
            editor.apply()
            Navigation.findNavController(it).navigate(R.id.action_navigation_list_to_navigation_map)
        }

        //вызов диалогового окна при клике на кнопку Delete.
        holder.itemView.button_delete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Удаление данных")
            builder.setMessage("Вы действительно хотите удалить данные?")

            //события при клике на "отмена". Выход из диалогового окна
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }

            //события при клике на "удалить". Удаление данных из room
            builder.setPositiveButton("Удалить") { _, _ ->
                val id = listGeolocation[position].id
                GeolocationDatabase.getInstance(Application()).getGeolocationDao().deleteById(id)
            }
            builder.show()

        }

        //вызов диалогового окна при клике на кнопку Update.
        holder.itemView.button_update.setOnClickListener {
            val id = listGeolocation[position].id

            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Изменение название точки")
            builder.setMessage("Введите ваше название")
            val editTextDialog = EditText(it.context)
            editTextDialog.setText(listGeolocation[position].title)
            builder.setView(editTextDialog)

            //события при клике на "отмена". Выход из диалогового окна
            builder.setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }

            //события при клике на "подтвердить". Изменение данных в room
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
    fun setList(list: List<GeolocationModel>) {
        listGeolocation = list
        //обновление при изменении данных
        notifyDataSetChanged()
    }
}