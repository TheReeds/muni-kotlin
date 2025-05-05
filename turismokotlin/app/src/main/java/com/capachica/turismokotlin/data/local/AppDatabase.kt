package com.capachica.turismokotlin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.capachica.turismokotlin.data.local.dao.EmprendedorDao
import com.capachica.turismokotlin.data.local.dao.EmprendedorMunicipalidadDao
import com.capachica.turismokotlin.data.local.dao.MunicipalidadDao
import com.capachica.turismokotlin.data.local.entity.EmprendedorEntity
import com.capachica.turismokotlin.data.local.entity.EmprendedorMunicipalidadRef
import com.capachica.turismokotlin.data.local.entity.MunicipalidadEntity

@Database(
    entities = [
        MunicipalidadEntity::class,
        EmprendedorEntity::class,
        EmprendedorMunicipalidadRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun municipalidadDao(): MunicipalidadDao
    abstract fun emprendedorDao(): EmprendedorDao
    abstract fun emprendedorMunicipalidadDao(): EmprendedorMunicipalidadDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "turismo_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}