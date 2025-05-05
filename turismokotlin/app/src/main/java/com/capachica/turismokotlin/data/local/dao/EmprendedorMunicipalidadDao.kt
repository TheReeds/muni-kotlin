package com.capachica.turismokotlin.data.local.dao

import androidx.room.*
import com.capachica.turismokotlin.data.local.entity.EmprendedorMunicipalidadRef
import com.capachica.turismokotlin.data.model.EmprendedorBasic
import kotlinx.coroutines.flow.Flow

@Dao
interface EmprendedorMunicipalidadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ref: EmprendedorMunicipalidadRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(refs: List<EmprendedorMunicipalidadRef>)

    @Delete
    suspend fun delete(ref: EmprendedorMunicipalidadRef)

    @Query("DELETE FROM emprendedor_municipalidad_ref WHERE emprendedorId = :emprendedorId")
    suspend fun deleteByEmprendedorId(emprendedorId: Long)

    @Query("DELETE FROM emprendedor_municipalidad_ref WHERE municipalidadId = :municipalidadId")
    suspend fun deleteByMunicipalidadId(municipalidadId: Long)

    @Transaction
    @Query("""
        SELECT e.id, e.nombreEmpresa, e.rubro
        FROM emprendedores e
        INNER JOIN emprendedor_municipalidad_ref ref ON e.id = ref.emprendedorId
        WHERE ref.municipalidadId = :municipalidadId
    """)
    fun getEmprendedoresByMunicipalidadId(municipalidadId: Long): Flow<List<EmprendedorBasic>>

    @Query("SELECT EXISTS(SELECT 1 FROM emprendedor_municipalidad_ref WHERE emprendedorId = :emprendedorId AND municipalidadId = :municipalidadId)")
    suspend fun exists(emprendedorId: Long, municipalidadId: Long): Boolean
}