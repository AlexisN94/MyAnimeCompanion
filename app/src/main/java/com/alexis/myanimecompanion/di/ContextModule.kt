package com.alexis.myanimecompanion.di

import android.content.Context
import android.content.SharedPreferences
import com.alexis.myanimecompanion.createEncryptedSharedPreferences
import dagger.Module
import dagger.Provides

@Module
class ContextModule(private val context: Context) {
    @Provides fun provideContext(): Context {
        return context
    }

    @Provides fun provideEncryptedSharedPreferences(): SharedPreferences {
        return createEncryptedSharedPreferences(context)
    }
}
