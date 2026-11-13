package com.jasiri.fieldofficer.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    // TODO: wire in SQLCipher (net.zetetic:sqlcipher-android) via Room's
    // openHelperFactory before any real customer ID/KYC data is cached here —
    // this is called out as a pre-pilot hardening task in
    // docs/Field-Officer-Mobile-App-Plan.pdf §9/§12.
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "jasiri_field_officer.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCustomerDao(db: AppDatabase) = db.customerDao()

    @Provides
    fun provideOutboxDao(db: AppDatabase) = db.outboxDao()
}
