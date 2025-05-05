package com.capachica.turismokotlin.data.local.dao

import androidx.room.*
import com.capachica.turismokotlin.data.local.entity.EmprendedorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmprendedorDao {
    @Query("SELECT * FROM emprendedores")
    fun getAllEmprendedores(): Flow<List<EmprendedorEntity>>

    @Query("SELECT * FROM emprendedores WHERE id = :id")
    fun getEmprendedorById(id: Long): Flow<EmprendedorEntity?>

    @Query("SELECT * FROM emprendedores WHERE municipalidadId = :municipalidadId")
    fun getEmprendedoresByMunicipalidad(municipalidadId: Long): Flow<List<EmprendedorEntity>>

    @Query("SELECT * FROM emprendedores WHERE rubro = :rubro")
    fun getEmprendedoresByRubro(rubro: String): Flow<List<EmprendedorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmprendedor(emprendedor: EmprendedorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmprendedores(emprendedores: List<EmprendedorEntity>)

    @Update
    suspend fun updateEmprendedor(emprendedor: EmprendedorEntity)

    @Delete
    suspend fun deleteEmprendedor(emprendedor: EmprendedorEntity)

    @Query("DELETE FROM emprendedores WHERE id = :id")
    suspend fun deleteEmprendedorById(id: Long)
}