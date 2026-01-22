/*
ACR-a3ebd7a923e34b6e81d3a98c46963bba
ACR-b992ad937a7a4f269cc87e64a2060239
ACR-83a51001f352424281c0c579eb66809d
ACR-8d333f7d83a14bd7a127ce77056c0690
ACR-b3c82e2c0c694e9e957f48256ddf08ff
ACR-baa7c2800642404b945a977c295ac2bc
ACR-ecb358c9fddc45118e1b452fd6f4e1ac
ACR-2fb6f2e5b3a740229b558bc562d72324
ACR-07b0911f246a46af84a3908ccc88b552
ACR-79ce762fffab47aaad74504ed94ee887
ACR-132dc77585f643d18c8ea2e59f1f8be4
ACR-b78f5de35ac14d0796b130ef21b3c251
ACR-d0ba9d385d33417ead63a73b68ea0e34
ACR-95408904e2e34d6eac86ac9b994f9ee5
ACR-6d611b8ef46c4d03b1c24e897ec4132f
ACR-c389df076bc5476c848c31b6d24548f2
ACR-79b499e18b0f4a728f306b922cb9904b
 */
package org.sonarsource.sonarlint.core.plugin.commons.container;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public interface ExtensionContainer extends Container {
  ExtensionContainer addExtension(@Nullable String pluginKey, Object extension);

  ExtensionContainer declareProperties(Object extension);

  @Override
  @CheckForNull
  ExtensionContainer getParent();
}
