package com.udacity.asteroidradar.respository

import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.ApodApi
import com.udacity.asteroidradar.api.NwoApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Locale


const val DATE_FORMAT = "YYYY-MM-dd"


class AsteroidRepository(private val database: AsteroidDatabase) {
    val asteroids: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroids().map {
        it.asDomainModel()
    }

    val asteroidsToday: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroidsToday(getTodayDate()).map {
        it.asDomainModel()
    }

    val asteroidsWeek: LiveData<List<Asteroid>> = database.asteroidDao.getAsteroidsWeek(getTodayDate()).map {
        it.asDomainModel()
    }

    val pictureOfDay: LiveData<DatabasePictureOfDay?> = database.pictureOfDayDao.getPictureOfDay()

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroids = NwoApi.retrofitService.getAsteroids(getTodayDate())
            database.asteroidDao.insertAll(*parseAsteroidsJsonResult(JSONObject(asteroids)).asDatabaseModel())
        }
    }

    suspend fun refreshPictureOfDay() {
        withContext(Dispatchers.IO) {
            val apodResponse = ApodApi.retrofitService.getPicOfTheDay()
            if (apodResponse.isSuccessful) {
                apodResponse.body()?.let { pictureOfDay ->
                    if (pictureOfDay.mediaType == "image") {
                        database.pictureOfDayDao.insert(pictureOfDay.asDatabaseModel())
                    }
                }
            }
        }
    }
}



private fun getTodayDate(): String{
    val cal = Calendar.getInstance()
    val currentTime = cal.time
    val dateFormat = android.icu.text.SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentTime)

}