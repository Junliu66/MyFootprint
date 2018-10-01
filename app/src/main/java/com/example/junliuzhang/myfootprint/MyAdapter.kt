package com.example.junliuzhang.myfootprint

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide


class MyAdapter(private val context: Context, private val uploads: List<String>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_images, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upload = uploads[position]

        //holder.textViewName.setText(upload.getName())

        Glide.with(context).load(upload).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return uploads.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textViewName: TextView
        var imageView: ImageView

        init {

            textViewName = itemView.findViewById<View>(R.id.textViewName) as TextView
            imageView = itemView.findViewById<View>(R.id.imageView) as ImageView
        }
    }
}
