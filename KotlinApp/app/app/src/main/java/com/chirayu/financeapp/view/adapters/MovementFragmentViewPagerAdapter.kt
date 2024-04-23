package com.chirayu.financeapp.view.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chirayu.financeapp.view.NewIncomeFragment
import com.chirayu.financeapp.view.NewMovementFragment

class MovementFragmentViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle : Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle){
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0)
            NewIncomeFragment()
        else
            NewMovementFragment()
    }
}