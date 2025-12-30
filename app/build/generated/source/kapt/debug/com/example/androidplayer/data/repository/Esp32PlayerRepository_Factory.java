package com.example.androidplayer.data.repository;

import com.example.androidplayer.data.network.Esp32PlayerApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
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
public final class Esp32PlayerRepository_Factory implements Factory<Esp32PlayerRepository> {
  private final Provider<Esp32PlayerApi> apiProvider;

  public Esp32PlayerRepository_Factory(Provider<Esp32PlayerApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public Esp32PlayerRepository get() {
    return newInstance(apiProvider.get());
  }

  public static Esp32PlayerRepository_Factory create(Provider<Esp32PlayerApi> apiProvider) {
    return new Esp32PlayerRepository_Factory(apiProvider);
  }

  public static Esp32PlayerRepository newInstance(Esp32PlayerApi api) {
    return new Esp32PlayerRepository(api);
  }
}
