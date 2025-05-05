package com.capachica.turismokotlin.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.capachica.turismokotlin.data.model.Municipalidad
import com.capachica.turismokotlin.data.model.EmprendedorBasic

@Entity(tableName = "municipalidades")
data class MunicipalidadEntity(
    @PrimaryKey val id: Long,
    val nombre: String,
    val departamento: String,
    val provincia: String,
    val distrito: String,
    val direccion: String?,
    val telefono: String?,
    val sitioWeb: String?,
    val descripcion: String?,
    val usuarioId: Long,
    // Relaciones se manejan en una tabla de relaciones
    val timestampUltimaActualizacion: Long = System.currentTimeMillis()
) {
    fun toModel(emprendedores: List<EmprendedorBasic> = emptyList()): Municipalidad {
        return Municipalidad(
            id = id,
            nombre = nombre,
            departamento = departamento,
            provincia = provincia,
            distrito = distrito,
            direccion = direccion,
            telefono = telefono,
            sitioWeb = sitioWeb,
            descripcion = descripcion,
            usuarioId = usuarioId,
            emprendedores = emprendedores
        )
    }

    companion object {
        fun fromModel(model: Municipalidad): MunicipalidadEntity {
            return MunicipalidadEntity(
                id = model.id,
                nombre = model.nombre,
                departamento = model.departamento,
                provincia = model.provincia,
                distrito = model.distrito,
                direccion = model.direccion,
                telefono = model.telefono,
                sitioWeb = model.sitioWeb,
                descripcion = model.descripcion,
                usuarioId = model.usuarioId
            )
        }
    }
}
