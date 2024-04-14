package com.chirayu.financeapp.data.repository

import androidx.annotation.WorkerThread
import com.chirayu.financeapp.data.dao.TagDao
import com.chirayu.financeapp.model.entities.Tag
import kotlinx.coroutines.flow.Flow

class TagRepository(private val tagDao: TagDao) {
    val allTags: Flow<List<Tag>> = tagDao.getAll()

    @WorkerThread
    suspend fun getById(id: Int): Tag? {
        return tagDao.getById(id)
    }

    @WorkerThread
    suspend fun insert(tag: Tag) {
        tagDao.insert(tag)
    }

    @WorkerThread
    suspend fun update(tag: Tag) {
        tagDao.update(tag)
    }

    @WorkerThread
    suspend fun delete(tag: Tag) {
        tagDao.delete(tag)
    }

    @WorkerThread
    suspend fun deleteAll() {
        tagDao.deleteAll()
    }
}
