package com.capachica.turismokotlin.data.local.dao

import androidx.room.*
import com.capachica.turismokotlin.data.local.entity.MunicipalidadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MunicipalidadDao {
    @Query("SELECT * FROM municipalidades")
    fun getAllMunicipalidades(): Flow<List<MunicipalidadEntity>>

    @Query("SELECT * FROM municipalidades WHERE id = :id")
    fun getMunicipalidadById(id: Long): Flow<MunicipalidadEntity?>

    @Query("SELECT * FROM municipalidades WHERE departamento = :departamento")
    fun getMunicipalidadesByDepartamento(departamento: String): Flow<List<MunicipalidadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMunicipalidad(municipalidad: MunicipalidadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMunicipalidades(municipalidades: List<MunicipalidadEntity>)

    @Update
    suspend fun updateMunicipalidad(municipalidad: MunicipalidadEntity)

    @Delete
    suspend fun deleteMunicipalidad(municipalidad: MunicipalidadEntity)

    @Query("DELETE FROM municipalidades WHERE id = :id")
    suspend fun deleteMunicipalidadById(id: Long)
}