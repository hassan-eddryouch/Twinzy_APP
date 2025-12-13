package com.example.twinzy_app.data.local

import androidx.room.*
import com.example.twinzy_app.data.model.Gender
import com.example.twinzy_app.data.model.Location
import com.example.twinzy_app.data.model.Preferences
import kotlinx.coroutines.flow.Flow

// Entities
@Entity(tableName = "cached_users")
data class CachedUserEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val age: Int,
    val bio: String,
    val gender: String,
    val interests: String, // JSON string
    val photos: String, // JSON string
    val latitude: Double,
    val longitude: Double,
    val city: String,
    val country: String,
    val cachedAt: Long
)

@Entity(tableName = "swipe_history")
data class SwipeHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val targetUserId: String,
    val isLike: Boolean,
    val isSuperLike: Boolean,
    val timestamp: Long
)

// DAOs
@Dao
interface CachedUserDao {
    @Query("SELECT * FROM cached_users WHERE uid = :userId")
    suspend fun getUserById(userId: String): CachedUserEntity?

    @Query("SELECT * FROM cached_users ORDER BY cachedAt DESC LIMIT :limit")
    fun getAllUsers(limit: Int = 20): Flow<List<CachedUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: CachedUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<CachedUserEntity>)

    @Query("DELETE FROM cached_users WHERE cachedAt < :expiryTime")
    suspend fun deleteExpiredUsers(expiryTime: Long)

    @Query("DELETE FROM cached_users")
    suspend fun clearAll()
}

@Dao
interface SwipeHistoryDao {
    @Query("SELECT * FROM swipe_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getSwipeHistory(userId: String): Flow<List<SwipeHistoryEntity>>

    @Query("SELECT targetUserId FROM swipe_history WHERE userId = :userId")
    suspend fun getSwipedUserIds(userId: String): List<String>

    @Insert
    suspend fun insertSwipe(swipe: SwipeHistoryEntity)

    @Query("DELETE FROM swipe_history WHERE userId = :userId AND targetUserId = :targetUserId")
    suspend fun deleteSwipe(userId: String, targetUserId: String)

    @Query("DELETE FROM swipe_history WHERE timestamp < :expiryTime")
    suspend fun deleteOldSwipes(expiryTime: Long)
}

// Database
@Database(
    entities = [CachedUserEntity::class, SwipeHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TwinzyDatabase : RoomDatabase() {
    abstract fun cachedUserDao(): CachedUserDao
    abstract fun swipeHistoryDao(): SwipeHistoryDao
}

// Type Converters
class Converters {
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        return value.split(",").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return list.joinToString(",")
    }
}