/*
ACR-9ecbe5e64a444f0299e76ffd27c1bdcf
ACR-ba46ff3ab0594d7baaa32f6738341618
ACR-6f590fe01b644bcfb953dd3390fbc3be
ACR-714574a5c6ef48c09266343fba879871
ACR-acf56d20fdfc4c5fb22046a256c33b77
ACR-00f84f29c3a34734ab24d1a1d5d10135
ACR-db5def0cd909461abfa98700cde56d88
ACR-69a123f4e87042ca90aabd345829fd21
ACR-d3ffc9646a80459da493e6a62f0123e3
ACR-57365d918cc3495a89ed1299042a1094
ACR-b95ff4fa39e54f97b22909dd472a3d55
ACR-a6aea2070cd742ba8c75f7ddcf6c3221
ACR-ff72b242f2b14ab9a5410ce43f228b9e
ACR-c009f93696d7428699f30c7e5f6d0698
ACR-1d830ab665e94c5f8a762b220e51bae6
ACR-d77d081f2eb2455894d0fddeab9b8e53
ACR-b021568007174fe0931c9b20c18e7633
 */
package org.sonarsource.sonarlint.core.rpc.protocol.adapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;

import static org.sonarsource.sonarlint.core.rpc.protocol.Lsp4jUtils.isEither;

public class EitherTypeAdapter<L, R> extends TypeAdapter<Either<L, R>> {

  private final org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter<L, R> lsp4jEitherAdapter;

  public EitherTypeAdapter(Gson gson, TypeToken<? extends Either<L, R>> typeToken, @Nullable Predicate<JsonElement> leftChecker,
    @Nullable Predicate<JsonElement> rightChecker) {
    var eitherType = (ParameterizedType) typeToken.getType();
    var lsp4jEitherType = new ParameterizedTypeImpl(org.eclipse.lsp4j.jsonrpc.messages.Either.class, eitherType.getActualTypeArguments());
    var lsp4jTypeToken = (TypeToken<? extends org.eclipse.lsp4j.jsonrpc.messages.Either<L, R>>) TypeToken.get(lsp4jEitherType);
    this.lsp4jEitherAdapter = new org.eclipse.lsp4j.jsonrpc.json.adapters.EitherTypeAdapter<>(gson, lsp4jTypeToken, leftChecker,
      rightChecker, null, null);
  }

  public EitherTypeAdapter(Gson gson, TypeToken<? extends Either<L, R>> typeToken) {
    this(gson, typeToken, null, null);
  }

  public static class Factory implements TypeAdapterFactory {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      if (!isEither(typeToken.getType())) {
        return null;
      }
      return new EitherTypeAdapter(gson, typeToken);
    }
  }

  @Override
  public void write(JsonWriter out, Either<L, R> value) throws IOException {
    this.lsp4jEitherAdapter.write(out, value.getLsp4jEither());
  }

  @Override
  public Either<L, R> read(JsonReader in) throws IOException {
    return new Either<>(this.lsp4jEitherAdapter.read(in));
  }

  private static class ParameterizedTypeImpl implements ParameterizedType {

    private final Type rawType;
    private final Type[] actualTypeArguments;

    ParameterizedTypeImpl(Type rawType, Type[] typeArguments) {
      this.rawType = rawType;
      this.actualTypeArguments = typeArguments;
    }

    @Override
    public Type getOwnerType() {
      return null;
    }

    @Override
    public Type getRawType() {
      return rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
      return actualTypeArguments;
    }
  }

}
