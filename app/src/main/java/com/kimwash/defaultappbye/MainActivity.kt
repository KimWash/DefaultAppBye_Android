package com.kimwash.defaultappbye

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kimwash.defaultappbye.Util.isNightModeActive
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        if (isNightModeActive(this)){
            setTheme(R.style.DarkTheme)
        }
        else if(!isNightModeActive(this)){
            setTheme(R.style.LightTheme)
        }
        setContentView(R.layout.activity_main)


        val fm = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fragmentTransaction.add(R.id.Fragment, MainFragment())
        fragmentTransaction.commit()

        //Bnav init
        bottomNavigationView.setSelectedItemId(R.id.action_connect)
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            Log.d(TAG, "item clicked")
            switchFragment(menuItem.itemId)
            return@OnNavigationItemSelectedListener true
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

    }

    fun switchFragment(menuId:Int){
        var fr = Fragment()
        when (menuId){
            R.id.action_connect -> {
                fr = MainFragment()
            }
            R.id.action_settings -> {
                fr = SettingFragment()
            }
        }
        val fm = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fragmentTransaction.replace(R.id.Fragment, fr)
        fragmentTransaction.commit()
    }



}