package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.respository.AsteroidRepository

class RefreshDataWork(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
        companion object {
            const val WORK_NAME = "RefreshDataWorker"
        }
        override suspend fun doWork(): Result {
            val database = getDatabase(applicationContext)
            val repository = AsteroidRepository(database)
            return try {
                repository.refreshAsteroids()
                repository.refreshPictureOfDay()
                Result.success()
            }catch (e: Exception){
                Result.retry()
            }
        }
}