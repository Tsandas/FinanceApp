package com.example.financeapptestversion.di

import com.example.financeapptestversion.network.StocksApi
import com.example.financeapptestversion.repository.FireRepository
import com.example.financeapptestversion.repository.StockRepository
import com.example.financeapptestversion.utils.Constants
import com.example.financeapptestversion.utils.Constants.BASE_URL
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.annotation.Signed
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFireStockRepository() =
        FireRepository(queryStocks = FirebaseFirestore.getInstance().collection("stocks"))

    @Singleton
    @Provides
    fun provideStockRepository(api: StocksApi) = StockRepository(api)

    @Singleton
    @Provides
    fun provideStockApi(): StocksApi {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                okhttp3.OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val request = original.newBuilder()
                            .url(
                                original.url.newBuilder()
                                    .addQueryParameter("apikey", Constants.API_KEY)
                                    .build()
                            )
                            .build()
                        chain.proceed(request)
                    }
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .build()
            .create(StocksApi::class.java)
    }


}