package com.example.androidplayer.ui.viewmodel;

import com.example.androidplayer.data.ble.BlePlayerClient;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<Esp32PlayerRepository> repositoryProvider;

  private final Provider<BlePlayerClient> bleClientProvider;

  public PlayerViewModel_Factory(Provider<Esp32PlayerRepository> repositoryProvider,
      Provider<BlePlayerClient> bleClientProvider) {
    this.repositoryProvider = repositoryProvider;
    this.bleClientProvider = bleClientProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(repositoryProvider.get(), bleClientProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<Esp32PlayerRepository> repositoryProvider,
      Provider<BlePlayerClient> bleClientProvider) {
    return new PlayerViewModel_Factory(repositoryProvider, bleClientProvider);
  }

  public static PlayerViewModel newInstance(Esp32PlayerRepository repository,
      BlePlayerClient bleClient) {
    return new PlayerViewModel(repository, bleClient);
  }
}
