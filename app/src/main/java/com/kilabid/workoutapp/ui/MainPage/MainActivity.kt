package com.kilabid.workoutapp.ui.MainPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kilabid.workoutapp.databinding.ActivityMainBinding
import com.kilabid.workoutapp.ui.CameraFragment.CameraFragment
import com.kilabid.workoutapp.ui.PushUpPage.PushUpFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val container = binding.container
        val fragment = PushUpFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(container.id, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}