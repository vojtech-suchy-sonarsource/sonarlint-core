/*
ACR-78ed7c4c24474cd789ee35982af9f399
ACR-03c1a35f310745d08c612ba912e3dc6f
ACR-a2c8ecc8023e4fd19d82d1f58857a4e8
ACR-cfdee4742fc74c5d83f0c61286ad68da
ACR-4156c08c6a444d9fa1f22d6fb1e7c511
ACR-72c9dbd812174adb8b35aaad1b95cadb
ACR-53701c4ba3374d5b807d4c5b6b07f8cc
ACR-b4dee56ed9a240b2b5d419ddb3732d40
ACR-443108f54cd24cec9b0a2798ad8ff1d6
ACR-6f66b5f9022749c48a99d9f6c4e55244
ACR-77e08f34602e4b64b1758f61b0351cfa
ACR-54ce236473314625be8ef3692cb8822c
ACR-7e7d71cf18c64b76bb5d4dd8ea2f2065
ACR-2c149a66a3654363956875880ac2ecd5
ACR-82bd9eae3ba94249ad026af3f33ccb3d
ACR-8f8ad0c6b64d48fcad61d8b6dbc8a2ae
ACR-d8af4baf223c461dadbfcd6d4435c682
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

import static java.util.function.Predicate.not;
import static org.sonarsource.sonarlint.core.rpc.protocol.Lsp4jUtils.isEither;

public abstract class CustomEitherAdapterFactory<L, R> implements TypeAdapterFactory {

  private final TypeToken<Either<L, R>> elementType;
  private final Class<L> leftClass;
  private final Class<R> rightClass;
  private final Predicate<JsonElement> leftChecker;

  protected CustomEitherAdapterFactory(TypeToken<Either<L, R>> elementType, Class<L> leftClass,
    Class<R> rightClass, Predicate<JsonElement> leftChecker) {
    this.elementType = elementType;
    this.leftClass = leftClass;
    this.rightClass = rightClass;
    this.leftChecker = leftChecker;
  }

  @SuppressWarnings("unchecked")
  @Override
  public final <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    if (!isEither(type.getType())) {
      return null;
    }
    Type[] typeParameters = ((ParameterizedType) type.getType()).getActualTypeArguments();
    var leftType = typeParameters[0];
    var rightType = typeParameters[1];
    if (!leftClass.isAssignableFrom((Class<?>) leftType) && !rightClass.isAssignableFrom((Class<?>) rightType)) {
      return null;
    }
    return (TypeAdapter<T>) new EitherTypeAdapter<>(gson, elementType, leftChecker, not(leftChecker));
  }
}
