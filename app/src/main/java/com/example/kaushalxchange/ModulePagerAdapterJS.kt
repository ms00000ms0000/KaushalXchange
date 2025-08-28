package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ModulePagerAdapterJS(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1JS()
            1 -> Module2JS()
            2 -> Module3JS()
            3 -> Module4JS()
            4 -> Module5JS()
            5 -> Module6JS()
            else -> Module1JS()
        }
    }
}