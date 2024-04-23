package com.chirayu.financeapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.chirayu.financeapp.R
import com.chirayu.financeapp.databinding.FragmentMovementBinding
import com.chirayu.financeapp.view.adapters.MovementFragmentViewPagerAdapter


class MovementFragment : Fragment() {

    private var _binding : FragmentMovementBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter : MovementFragmentViewPagerAdapter

    private var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = MovementFragmentViewPagerAdapter(requireActivity().supportFragmentManager,lifecycle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovementBinding
            .inflate(inflater,container,false)
            .apply {
                lifecycleOwner = viewLifecycleOwner
            }

        setupUI()


        return binding.root
    }

    private fun setupUI() {

        binding.apply {

            tabIncomeButton.setOnClickListener {
                if(tabPosition == 0)
                    return@setOnClickListener

                tabPosition = 0
                changeTabsBackground()
                viewPager.currentItem = tabPosition
            }

            tabExpenseButton.setOnClickListener {
                if(tabPosition == 1)
                    return@setOnClickListener

                tabPosition = 1
                changeTabsBackground()
                viewPager.currentItem = tabPosition
            }

            viewPager.adapter = adapter
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabPosition = position
                    changeTabsBackground()
                }
            })
        }

    }

    private fun changeTabsBackground() {
        when(tabPosition) {
            0 -> {
                binding.apply {
                    tabIncomeButton.background = ResourcesCompat.getDrawable(resources,R.drawable.tab_indicator_active,null)
                    tabExpenseButton.background = ResourcesCompat.getDrawable(resources,R.drawable.tab_indicator,null)
                }
            }
            else -> {
                binding.apply {
                    tabIncomeButton.background = ResourcesCompat.getDrawable(resources,R.drawable.tab_indicator,null)
                    tabExpenseButton.background = ResourcesCompat.getDrawable(resources,R.drawable.tab_indicator_active,null)
                }
            }
        }
    }

}