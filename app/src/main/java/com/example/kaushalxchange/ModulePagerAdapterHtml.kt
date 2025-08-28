package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ModulePagerAdapterHtml(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1Html()
            1 -> Module2Html()
            2 -> Module3Html()
            3 -> Module4Html()
            4 -> Module5Html()
            5 -> Module6Html()
            else -> Module1Html()
        }
    }
}