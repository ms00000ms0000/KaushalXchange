package com.example.kaushalxchange

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ModulePagerAdapterJava(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 6

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Module1Java()
            1 -> Module2Java()
            2 -> Module3Java()
            3 -> Module4Java()
            4 -> Module5Java()
            5 -> Module6Java()
            else -> Module1Java()
        }
    }
}