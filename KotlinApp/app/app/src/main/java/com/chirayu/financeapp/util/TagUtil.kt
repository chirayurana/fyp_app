package com.chirayu.financeapp.util

import com.chirayu.financeapp.SaveAppApplication
import kotlinx.coroutines.launch

object TagUtil {
    val incomeTagIds: MutableSet<Int> = mutableSetOf()

    fun updateAll(application: SaveAppApplication) {
        incomeTagIds.clear()
        application.applicationScope.launch {
            application.tagRepository.allTags.collect {
                it.filter { tag -> tag.isIncome }.forEach { tag -> incomeTagIds.add(tag.id) }
            }
        }
    }
}
