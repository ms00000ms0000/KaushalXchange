package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ModulePagerAdapterCss(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1Css()
            1 -> Module2Css()
            2 -> Module3Css()
            3 -> Module4Css()
            4 -> Module5Css()
            5 -> Module6Css()
            else -> Module1Css()
        }
    }
}