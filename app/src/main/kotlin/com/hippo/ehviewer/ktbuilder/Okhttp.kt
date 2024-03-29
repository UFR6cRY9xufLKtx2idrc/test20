package com.hippo.ehviewer.ktbuilder

import com.google.net.cronet.okhttptransport.CronetInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okio.FileSystem
import okio.Path
import org.chromium.net.CronetEngine

inline fun httpClient(builder: OkHttpClient.Builder.() -> Unit): OkHttpClient = OkHttpClient.Builder().apply(builder).build()
inline fun httpClient(client: OkHttpClient, builder: OkHttpClient.Builder.() -> Unit): OkHttpClient = client.newBuilder().apply(builder).build()
fun OkHttpClient.Builder.cache(directory: Path, maxSize: Long, fileSystem: FileSystem = FileSystem.SYSTEM) = cache(Cache(directory, maxSize, fileSystem))

inline fun OkHttpClient.Builder.cronet(client: CronetEngine, builder: CronetInterceptor.Builder.() -> Unit = {}) = addInterceptor(CronetInterceptor.newBuilder(client).apply(builder).build())
