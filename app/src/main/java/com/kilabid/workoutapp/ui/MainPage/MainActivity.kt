package com.kilabid.workoutapp.ui.MainPage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.ui.CameraFragment.CameraFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.container, CameraFragment())
            }
        }
    }
}