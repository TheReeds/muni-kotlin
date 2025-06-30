package com.capachica.turismokotlin.di

import com.capachica.turismokotlin.network.api.AdminCategoriasApiService
import com.capachica.turismokotlin.network.api.AdminEmprendedoresApiService
import com.capachica.turismokotlin.network.api.AdminMunicipalidadesApiService
import com.capachica.turismokotlin.network.api.AdminUsuariosApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminModule {

    @Provides
    @Singleton
    fun provideAdminUsuariosApiService(retrofit: Retrofit): AdminUsuariosApiService {
        return retrofit.create(AdminUsuariosApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminEmprendedoresApiService(retrofit: Retrofit): AdminEmprendedoresApiService {
        return retrofit.create(AdminEmprendedoresApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminCategoriasApiService(retrofit: Retrofit): AdminCategoriasApiService {
        return retrofit.create(AdminCategoriasApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminMunicipalidadesApiService(retrofit: Retrofit): AdminMunicipalidadesApiService {
        return retrofit.create(AdminMunicipalidadesApiService::class.java)
    }
}