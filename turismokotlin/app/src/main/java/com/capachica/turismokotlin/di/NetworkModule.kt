package com.capachica.turismokotlin.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capachica.turismokotlin.BuildConfig
import com.capachica.turismokotlin.network.api.AuthApiService
import com.capachica.turismokotlin.network.api.CartApiService
import com.capachica.turismokotlin.network.api.CategoriasApiService
import com.capachica.turismokotlin.network.api.ChatApiService
import com.capachica.turismokotlin.network.api.EmprendedoresApiService
import com.capachica.turismokotlin.network.api.PlanesApiService
import com.capachica.turismokotlin.network.api.ReservasCarritoApiService
import com.capachica.turismokotlin.network.api.ReservasPlanesApiService
import com.capachica.turismokotlin.network.api.ServiciosApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.Interceptor
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(dataStore: DataStore<Preferences>): Interceptor {
        return Interceptor { chain ->
            val request = chain.request()
            val token = runBlocking {
                dataStore.data.first()[stringPreferencesKey("auth_token")]
            }

            val newRequest = if (token != null && !request.url.pathSegments.contains("login") && !request.url.pathSegments.contains("register")) {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }

            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePlanesApiService(retrofit: Retrofit): PlanesApiService {
        return retrofit.create(PlanesApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideServiciosApiService(retrofit: Retrofit): ServiciosApiService {
        return retrofit.create(ServiciosApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideEmprendedoresApiService(retrofit: Retrofit): EmprendedoresApiService {
        return retrofit.create(EmprendedoresApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoriasApiService(retrofit: Retrofit): CategoriasApiService {
        return retrofit.create(CategoriasApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideCartApiService(retrofit: Retrofit): CartApiService {
        return retrofit.create(CartApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReservasCarritoApiService(retrofit: Retrofit): ReservasCarritoApiService {
        return retrofit.create(ReservasCarritoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideReservasPlanesApiService(retrofit: Retrofit): ReservasPlanesApiService {
        return retrofit.create(ReservasPlanesApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideChatApiService(retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }
}