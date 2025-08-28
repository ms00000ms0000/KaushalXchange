package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ModulePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1Fragment()
            1 -> Module2Fragment()
            2 -> Module3Fragment()
            3 -> Module4Fragment()
            4 -> Module5Fragment()
            5 -> Module6Fragment()
            else -> Module1Fragment()
        }
    }
}
