package com.dicoding.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dicoding.storyapp.remote.response.ParcelableListStoryItem
import com.dicoding.storyapp.R

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val itemData = intent.getParcelableExtra<ParcelableListStoryItem>("ITEM_DATA")

        val photoImageView: ImageView = findViewById(R.id.iv_detail_photo)
        val usernameTextView: TextView = findViewById(R.id.tv_detail_username)
        val descriptionTextView: TextView = findViewById(R.id.tv_detail_description)

        itemData?.let {
            Glide.with(this).load(it.photoUrl).into(photoImageView)

            usernameTextView.text = it.name
            descriptionTextView.text = it.description
        }
    }
}