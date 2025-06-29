package com.capachica.turismokotlin.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.capachica.turismokotlin.data.model.Emprendedor
import com.capachica.turismokotlin.data.model.MunicipalidadBasic
import com.capachica.turismokotlin.data.model.CategoriaBasic

@Entity(
    tableName = "emprendedores",
    foreignKeys = [
        ForeignKey(
            entity = MunicipalidadEntity::class,
            parentColumns = ["id"],
            childColumns = ["municipalidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("municipalidadId"), Index("categoriaId")] // Agregamos índice para mejor rendimiento
)
data class EmprendedorEntity(
    @PrimaryKey val id: Long,
    val nombreEmpresa: String,
    val rubro: String,
    val direccion: String?,
    val latitud: Double?,
    val longitud: Double?,
    val direccionCompleta: String?,
    val telefono: String?,
    val email: String?,
    val sitioWeb: String?,
    val descripcion: String?,
    val productos: String?,
    val servicios: String?,
    val usuarioId: Long,
    val municipalidadId: Long,
    val categoriaId: Long?,
    val timestampUltimaActualizacion: Long = System.currentTimeMillis()
) {
    fun toModel(municipalidad: MunicipalidadBasic? = null, categoria: CategoriaBasic? = null): Emprendedor {
        return Emprendedor(
            id = id,
            nombreEmpresa = nombreEmpresa,
            rubro = rubro,
            direccion = direccion,
            latitud = latitud,
            longitud = longitud,
            direccionCompleta = direccionCompleta,
            telefono = telefono,
            email = email,
            sitioWeb = sitioWeb,
            descripcion = descripcion,
            productos = productos,
            servicios = servicios,
            usuarioId = usuarioId,
            municipalidad = municipalidad,
            categoria = categoria
        )
    }

    companion object {
        fun fromModel(model: Emprendedor): EmprendedorEntity {
            return EmprendedorEntity(
                id = model.id,
                nombreEmpresa = model.nombreEmpresa,
                rubro = model.rubro,
                direccion = model.direccion,
                latitud = model.latitud,
                longitud = model.longitud,
                direccionCompleta = model.direccionCompleta,
                telefono = model.telefono,
                email = model.email,
                sitioWeb = model.sitioWeb,
                descripcion = model.descripcion,
                productos = model.productos,
                servicios = model.servicios,
                usuarioId = model.usuarioId,
                municipalidadId = model.municipalidad?.id ?: 0,
                categoriaId = model.categoria?.id
            )
        }
    }
}