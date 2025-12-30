package com.example.androidplayer.di;

import android.content.Context;
import com.google.gson.Gson;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "javax.inject.Named",
    "dagger.hilt.android.qualifiers.ApplicationContext"
})
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class NetworkModuleDi_ProvideRetrofitFactory implements Factory<Retrofit> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private final Provider<Gson> gsonProvider;

  private final Provider<Context> contextProvider;

  public NetworkModuleDi_ProvideRetrofitFactory(Provider<OkHttpClient> okHttpClientProvider,
      Provider<Gson> gsonProvider, Provider<Context> contextProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
    this.gsonProvider = gsonProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public Retrofit get() {
    return provideRetrofit(okHttpClientProvider.get(), gsonProvider.get(), contextProvider.get());
  }

  public static NetworkModuleDi_ProvideRetrofitFactory create(
      Provider<OkHttpClient> okHttpClientProvider, Provider<Gson> gsonProvider,
      Provider<Context> contextProvider) {
    return new NetworkModuleDi_ProvideRetrofitFactory(okHttpClientProvider, gsonProvider, contextProvider);
  }

  public static Retrofit provideRetrofit(OkHttpClient okHttpClient, Gson gson, Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModuleDi.INSTANCE.provideRetrofit(okHttpClient, gson, context));
  }
}
