package com.ari_d.justeatit.ui.Profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.ari_d.justeatit.R
import com.ari_d.justeatit.ui.Profile.fragments.main_profile_fragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_JustEatIt_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        setCurrentFragment(main_profile_fragment())
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    internal fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            replace(R.id.fragment_container, fragment)
            commit()
        }
    }
}