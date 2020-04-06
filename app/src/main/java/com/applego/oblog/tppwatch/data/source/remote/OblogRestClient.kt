package com.applego.oblog.tppwatch.data.source.remote

import com.applego.oblog.tppwatch.data.source.remote.serializer.TppDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppListDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppServiceDeserializer
import com.applego.oblog.tppwatch.data.source.remote.serializer.TppsListResponseDeserializer
import com.applego.oblog.tppwatch.util.RetrofitTypes
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

object OblogRestClient {

    private val okHttpClient by lazy { OkHttpClient() }

    fun getBaseUrl(currentEnv: String, envsBaseUrls: Array<String>) : String {

        // HACK-HACK-HACK - because the statement above returns the preference ID instead if the value ... some times
        var theEnv: String = if (currentEnv.startsWith("@")) currentEnv else "TEST"


        /* This requires API level 21, while we go with 14

            val envsTypedArray = context.applicationContext.resources.obtainTypedArray(com.applego.oblog.tppwatch.R.array.environments)
            if (envsTypedArray != null) {
                for (iL: Int in 0..envsTypedArray.indexCount) {
                    val type = envsTypedArray.getType(iL)
                    Array<String> = envsTypedArray[i]
                }
            }
        */

        var baseUrl = ""
        if (envsBaseUrls != null) {
            for (i in envsBaseUrls.indices) {
                if (envsBaseUrls[i].startsWith(theEnv.toUpperCase())) {
                    baseUrl = envsBaseUrls[i].substring(theEnv.length+1)
                }
            }
        }

        return baseUrl
    }


    fun createRetrofit() : Gson {

        return GsonBuilder()
                .registerTypeAdapter(RetrofitTypes.tppType, TppDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppListType, TppListDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppsListResponseType, TppsListResponseDeserializer())
                .registerTypeAdapter(RetrofitTypes.tppServiceType, TppServiceDeserializer())
                .enableComplexMapKeySerialization()
                .serializeNulls()
                .setDateFormat(DateFormat.LONG)
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .setVersion(1.0)
                .create()
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    val certs: Array<X509Certificate> = emptyArray()
                    return certs
                }

                //val acceptedIssuers: Array<java.security.cert.X509Certificate>
                    //get() = arrayOfNulls<X509Certificate>(0)

                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>,
                                       authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>,
                                       authType: String) {
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            return OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                    .hostnameVerifier(object : HostnameVerifier {
                        override fun verify(hostname: String, session: SSLSession): Boolean {
                            return true
                        }
                    }).build()

        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun createRetrofit(baseUrl: String, restContext : String) : Retrofit {

        /*val trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(ManagerFactoryParameters)

        val trustManagers = trustManagerFactory.getTrustManagers();

        if (trustManagers.size != 1 || !(trustManagers[0] is X509TrustManager)) {
            throw IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        val trustManager = trustManagers[0] as X509TrustManager


        val sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null,  trustManagers, null);
        val sslSocketFactory = sslContext.getSocketFactory();
        */

        val okHttpClient = getUnsafeOkHttpClient()

                /*okHttpClient.newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                //.sslSocketFactory(sslSocketFactory, trustManager)
                .build()*/

        return Retrofit.Builder()
                .addCallAdapterFactory(
                        RxJava2CallAdapterFactory.create())
                .addConverterFactory(
                        GsonConverterFactory.create(createRetrofit()))
                .baseUrl(baseUrl + restContext)
                .client(okHttpClient)
                .build()

    }

}