package com.udacity.asteroidradar.main

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.DatabasePictureOfDay
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.respository.AsteroidRepository
import kotlinx.coroutines.launch

enum class AsteroidsFilter {TODAY, WEEK, ALL}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)
    private val asteroidsFilter = MutableLiveData(AsteroidsFilter.ALL)



    init {
        if (isOnline(application)) {
            viewModelScope.launch {
                asteroidRepository.refreshAsteroids()
                asteroidRepository.refreshPictureOfDay()
            }
        } else {
            Toast.makeText(
                application,
                "No internet connection. Showing cached data if any",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val asteroids : LiveData<List<Asteroid>> = asteroidsFilter.switchMap{
        when (it) {
            AsteroidsFilter.TODAY -> asteroidRepository.asteroidsToday
            AsteroidsFilter.WEEK -> asteroidRepository.asteroidsWeek
            else -> asteroidRepository.asteroids
        }
    }
    val pictureOfDay: LiveData<DatabasePictureOfDay?> = asteroidRepository.pictureOfDay

    fun updateFilter(filter: AsteroidsFilter) {
        asteroidsFilter.value = filter
    }





    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    return true
                }
            }
        }
        return false
    }
}