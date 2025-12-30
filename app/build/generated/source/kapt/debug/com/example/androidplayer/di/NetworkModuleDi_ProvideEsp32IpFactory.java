package com.example.androidplayer.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class NetworkModuleDi_ProvideEsp32IpFactory implements Factory<String> {
  private final Provider<Context> contextProvider;

  public NetworkModuleDi_ProvideEsp32IpFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public String get() {
    return provideEsp32Ip(contextProvider.get());
  }

  public static NetworkModuleDi_ProvideEsp32IpFactory create(Provider<Context> contextProvider) {
    return new NetworkModuleDi_ProvideEsp32IpFactory(contextProvider);
  }

  public static String provideEsp32Ip(Context context) {
    return Preconditions.checkNotNullFromProvides(NetworkModuleDi.INSTANCE.provideEsp32Ip(context));
  }
}
