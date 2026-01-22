/*
ACR-3dd90c7b36454f54aea54fac5272c1b4
ACR-1fe8a9ee4266449faba37ee5ad78b242
ACR-24bdb1c88ea04b8dae66f456851a2321
ACR-7380cb42b0a24c378c80fdb5a1dfff58
ACR-aebc891d6dc64c049f17fae7d9e55d4a
ACR-18f58b96e6c2440ba3274d193fd31012
ACR-18420fd8945443d184db15fa644584fa
ACR-4bd0d9b4dbb842f39b500b4932fa72d5
ACR-4d7e2b00d1684a4da1508bbeb72f8b49
ACR-3e77a3cc05074cf58b2719bf8204fce3
ACR-4865b10bc44f43dfb930bdd2e1b3c283
ACR-1f9dd6615fc84c2a94f84b18cb25857a
ACR-b3861bd612af4d1b9958955a9ef424c3
ACR-9dea5133d0d94b1d88a5b16be1a7f42e
ACR-4d45418dc80441fe87a69d8f85c99fe8
ACR-df174e5303024a159cb091cf9e4106b6
ACR-0b01b31ee6db4a389a3d2e72a5dcb82f
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
