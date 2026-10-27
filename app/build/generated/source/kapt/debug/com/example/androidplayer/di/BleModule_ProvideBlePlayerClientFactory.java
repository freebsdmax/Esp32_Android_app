package com.example.androidplayer.di;

import android.content.Context;
import com.example.androidplayer.data.ble.BlePlayerClient;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class BleModule_ProvideBlePlayerClientFactory implements Factory<BlePlayerClient> {
  private final Provider<Context> contextProvider;

  public BleModule_ProvideBlePlayerClientFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BlePlayerClient get() {
    return provideBlePlayerClient(contextProvider.get());
  }

  public static BleModule_ProvideBlePlayerClientFactory create(Provider<Context> contextProvider) {
    return new BleModule_ProvideBlePlayerClientFactory(contextProvider);
  }

  public static BlePlayerClient provideBlePlayerClient(Context context) {
    return Preconditions.checkNotNullFromProvides(BleModule.INSTANCE.provideBlePlayerClient(context));
  }
}
