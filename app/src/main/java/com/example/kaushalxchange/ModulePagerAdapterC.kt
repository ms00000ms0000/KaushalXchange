package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ModulePagerAdapterC(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1C()
            1 -> Module2C()
            2 -> Module3C()
            3 -> Module4C()
            4 -> Module5C()
            5 -> Module6C()
            else -> Module1C()
        }
    }
}