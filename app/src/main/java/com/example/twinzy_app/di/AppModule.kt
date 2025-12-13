package com.example.twinzy_app.di

import android.content.Context
import androidx.room.Room
import com.example.twinzy_app.config.SecurityConfig
import com.example.twinzy_app.data.local.TwinzyDatabase
import com.example.twinzy_app.utils.CloudinaryManager
import com.example.twinzy_app.data.manager.PreferencesManager
import com.example.twinzy_app.data.repository.*
import com.example.twinzy_app.utils.ErrorHandler
import com.example.twinzy_app.utils.InputValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging = FirebaseMessaging.getInstance()
    
    @Provides
    @Singleton
    fun provideCloudinaryManager(
        @ApplicationContext context: Context,
        securityConfig: SecurityConfig,
        errorHandler: ErrorHandler
    ): CloudinaryManager {
        return CloudinaryManager(context, securityConfig, errorHandler)
    }
    
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    
    @Provides
    @Singleton
    fun provideInputValidator(): InputValidator {
        return InputValidator()
    }
    
    @Provides
    @Singleton
    fun provideErrorHandler(): ErrorHandler {
        return ErrorHandler()
    }
    
    @Provides
    @Singleton
    fun provideSecurityConfig(@ApplicationContext context: Context): SecurityConfig {
        return SecurityConfig(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging,
        inputValidator: InputValidator,
        errorHandler: ErrorHandler
    ): AuthRepository {
        return AuthRepositoryImpl(auth, firestore, messaging, inputValidator, errorHandler)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
        cloudinaryManager: CloudinaryManager,
        inputValidator: InputValidator,
        errorHandler: ErrorHandler,
        auth: FirebaseAuth
    ): UserRepository {
        return UserRepositoryImpl(firestore, storage, cloudinaryManager, inputValidator, errorHandler, auth)
    }
    
    @Provides
    @Singleton
    fun provideDiscoverRepository(
        firestore: FirebaseFirestore,
        userRepository: UserRepository
    ): DiscoverRepository {
        return DiscoverRepositoryImpl(firestore, userRepository)
    }
    
    @Provides
    @Singleton
    fun provideTwinzyDatabase(@ApplicationContext context: Context): TwinzyDatabase {
        return Room.databaseBuilder(
            context,
            TwinzyDatabase::class.java,
            "twinzy_database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideMatchRepository(
        firestore: FirebaseFirestore,
        messaging: FirebaseMessaging
    ): MatchRepository {
        return MatchRepositoryImpl(firestore, messaging)
    }
    
    @Provides
    @Singleton
    fun provideChatRepository(
        firestore: FirebaseFirestore
    ): ChatRepository {
        return ChatRepositoryImpl(firestore)
    }
    
    @Provides
    @Singleton
    fun provideSwipeRepository(
        firestore: FirebaseFirestore,
        database: TwinzyDatabase
    ): SwipeRepository {
        return SwipeRepositoryImpl(firestore, database)
    }
    
    @Provides
    @Singleton
    fun providePreferencesManager(@ApplicationContext context: Context): PreferencesManager {
        return PreferencesManager(context)
    }
}