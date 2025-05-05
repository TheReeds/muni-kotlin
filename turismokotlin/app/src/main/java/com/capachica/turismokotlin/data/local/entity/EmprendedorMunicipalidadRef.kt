package com.capachica.turismokotlin.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "emprendedor_municipalidad_ref",
    primaryKeys = ["emprendedorId", "municipalidadId"],
    foreignKeys = [
        ForeignKey(
            entity = EmprendedorEntity::class,
            parentColumns = ["id"],
            childColumns = ["emprendedorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MunicipalidadEntity::class,
            parentColumns = ["id"],
            childColumns = ["municipalidadId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmprendedorMunicipalidadRef(
    val emprendedorId: Long,
    val municipalidadId: Long
)