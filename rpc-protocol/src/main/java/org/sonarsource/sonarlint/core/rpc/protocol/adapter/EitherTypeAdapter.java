/*
ACR-63c65ee4a7914137a26d24ab615b7c59
ACR-92b7bda190eb471f9debf6822b7122c0
ACR-19daeca79d0348dfa7c0108f1e4a4a95
ACR-c553aade4e854e1b94ddb83c9788551c
ACR-c2a575b9654a42fd8c43fd798bad206f
ACR-cc92f0d2518e4429ae99f5c6e165dbd2
ACR-b8940a2584a24dc081e4d5fa15bed40e
ACR-cfbd51abf9ac430389fef1078b710ddf
ACR-9c0db247753c42b09e808609066fa27d
ACR-12b32859173c49589fa01b3a85527be3
ACR-568e2019a0814b519ce747f1d70d66fd
ACR-7cc6270bf46d429e85b01952f1daf8bc
ACR-629d843a37214fbdb6326d6b6a01b61e
ACR-15cb41eb6db24f13b8d687d3360883ee
ACR-bc9ee55a0ec64671a9c3e4bd15d72c97
ACR-e7917acfeafd4cc8af7f52fde343a7a0
ACR-7312075eb51f49b7ad2978e0b4ed38c8
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
