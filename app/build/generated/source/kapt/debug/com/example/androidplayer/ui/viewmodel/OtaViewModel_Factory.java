package com.example.androidplayer.ui.viewmodel;

import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class OtaViewModel_Factory implements Factory<OtaViewModel> {
  private final Provider<Esp32PlayerRepository> repositoryProvider;

  public OtaViewModel_Factory(Provider<Esp32PlayerRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public OtaViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static OtaViewModel_Factory create(Provider<Esp32PlayerRepository> repositoryProvider) {
    return new OtaViewModel_Factory(repositoryProvider);
  }

  public static OtaViewModel newInstance(Esp32PlayerRepository repository) {
    return new OtaViewModel(repository);
  }
}
