package com.kilabid.workoutapp.ui.AboutPage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(getDrawable(R.drawable.foto_dev))
            .skipMemoryCache(true)
            .circleCrop()
            .into(binding.photoDev)

        binding.instagram.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.instagram.com/aldimurad/")
            startActivity(intent)
        }
        binding.linkedin.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.linkedin.com/in/aldi-murad-rifdiansyah-1a3006223/")
            startActivity(intent)
        }

    }
}