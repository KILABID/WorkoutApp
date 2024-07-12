package com.kilabid.workoutapp.ui.MainPage

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.kilabid.workoutapp.R
import com.kilabid.workoutapp.data.MainMenuData
import com.kilabid.workoutapp.databinding.ActivityMainBinding
import com.kilabid.workoutapp.ui.LoginPage.LoginPageActivity
import com.kilabid.workoutapp.ui.LoginPage.LoginViewModel
import com.kilabid.workoutapp.ui.PushUpPage.PushUpActivity
import com.kilabid.workoutapp.ui.SitUpPage.SitUpActivity
import com.kilabid.workoutapp.ui.SquatPage.SquatActivity
import com.kilabid.workoutapp.ui.ViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val list = ArrayList<MainMenuData>()
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logoutAction()
        list.addAll(getList())
        showListMenu()

//        System.loadLibrary()
        viewModel.getSession().observe(this){ user ->
            binding.tvHiUser.text = getString(R.string.hi_username, user.username)
        }

    }

    private fun logoutAction() {
        binding.logoout.setOnClickListener {
            lifecycleScope.launch {
                viewModel.logout()
                val intentOut = Intent(this@MainActivity, LoginPageActivity::class.java)
                startActivity(intentOut)
                finish()
            }
        }
    }

    @SuppressLint("Recycle")
    private fun getList(): ArrayList<MainMenuData> {
        val dataName = resources.getStringArray(R.array.list_menu)
        val dataImage = resources.obtainTypedArray(R.array.list_menu_iv)
        val dataDesc = resources.getStringArray(R.array.list_desc)
        val listMenu = ArrayList<MainMenuData>()
        for (i in dataName.indices) {
            val menu = MainMenuData(dataName[i], dataDesc[i], dataImage.getResourceId(i, -1))
            listMenu.add(menu)
        }
        return listMenu
    }

    private fun showListMenu() {
        binding.listMenu.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val listMenuAdapter = MainListAdapter(list) { clickedList ->
            when (clickedList.name) {
                "Sit-Up" -> {
                    val intentExcerisce = Intent(this, SitUpActivity::class.java)
                    startActivity(intentExcerisce)
                }

                "Push-Up" -> {
                    val intentExcerisce = Intent(this, PushUpActivity::class.java)
                    startActivity(intentExcerisce)
                }

                "Squat" -> {
                    val intentExcerisce = Intent(this, SquatActivity::class.java)
                    startActivity(intentExcerisce)
                }
            }
        }
        binding.listMenu.adapter = listMenuAdapter
    }
}