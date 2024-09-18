package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid order by closeApproachDate asc")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate = :startDate order by closeApproachDate asc")
    fun getAsteroidsToday(startDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate >= :startDate order by closeApproachDate asc")
    fun getAsteroidsWeek(startDate: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Dao
interface PictureOfDayDao{
    @Query("select * from databasepictureofday")
    fun getPictureOfDay(): LiveData<DatabasePictureOfDay?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg pictureOfDay: DatabasePictureOfDay)
}


@Database(entities = [DatabaseAsteroid::class, DatabasePictureOfDay::class], version = 2)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureOfDayDao: PictureOfDayDao
}


private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase{
    synchronized(AsteroidDatabase::class.java){
        if(!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java,
                "asteroids")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}