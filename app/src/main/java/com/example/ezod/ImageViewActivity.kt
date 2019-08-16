package com.example.ezod

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.piasy.biv.BigImageViewer
import com.github.piasy.biv.loader.glide.GlideImageLoader
import kotlinx.android.synthetic.main.activity_image_view.*

class ImageViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BigImageViewer.initialize(GlideImageLoader.with(this))
        setContentView(R.layout.activity_image_view)

        val intent = intent
        val imageFileUri = intent.getStringExtra("IMAGE_FILE")

        image_file_iv.showImage(Uri.parse(imageFileUri))

    }
}
