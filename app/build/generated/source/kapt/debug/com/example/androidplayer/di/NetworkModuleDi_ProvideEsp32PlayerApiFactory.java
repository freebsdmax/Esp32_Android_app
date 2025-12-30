package com.example.androidplayer.di;

import com.example.androidplayer.data.network.Esp32PlayerApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("javax.inject.Named")
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
public final class NetworkModuleDi_ProvideEsp32PlayerApiFactory implements Factory<Esp32PlayerApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModuleDi_ProvideEsp32PlayerApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public Esp32PlayerApi get() {
    return provideEsp32PlayerApi(retrofitProvider.get());
  }

  public static NetworkModuleDi_ProvideEsp32PlayerApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModuleDi_ProvideEsp32PlayerApiFactory(retrofitProvider);
  }

  public static Esp32PlayerApi provideEsp32PlayerApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModuleDi.INSTANCE.provideEsp32PlayerApi(retrofit));
  }
}
