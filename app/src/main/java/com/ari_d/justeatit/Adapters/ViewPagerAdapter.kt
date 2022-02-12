package com.ari_d.justeatit.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentActivity) :
        FragmentStateAdapter(supportFragmentManager) {

    private val fragments = ArrayList<Fragment>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment:  Fragment) {
        fragments.add(fragment)
    }
}