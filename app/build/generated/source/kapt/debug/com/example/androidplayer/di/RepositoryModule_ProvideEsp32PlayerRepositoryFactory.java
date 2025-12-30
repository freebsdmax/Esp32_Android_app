package com.example.androidplayer.di;

import com.example.androidplayer.data.network.Esp32PlayerApi;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class RepositoryModule_ProvideEsp32PlayerRepositoryFactory implements Factory<Esp32PlayerRepository> {
  private final Provider<Esp32PlayerApi> apiProvider;

  public RepositoryModule_ProvideEsp32PlayerRepositoryFactory(
      Provider<Esp32PlayerApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public Esp32PlayerRepository get() {
    return provideEsp32PlayerRepository(apiProvider.get());
  }

  public static RepositoryModule_ProvideEsp32PlayerRepositoryFactory create(
      Provider<Esp32PlayerApi> apiProvider) {
    return new RepositoryModule_ProvideEsp32PlayerRepositoryFactory(apiProvider);
  }

  public static Esp32PlayerRepository provideEsp32PlayerRepository(Esp32PlayerApi api) {
    return Preconditions.checkNotNullFromProvides(RepositoryModule.INSTANCE.provideEsp32PlayerRepository(api));
  }
}
