/*
ACR-dc32de12decf41dc9e980872af1b791e
ACR-34e823aa11194f4692f67b68f21ac689
ACR-b6a0fbee55aa43b3818879d81902f108
ACR-555f8262daea4efaaa63295fcb3b86ba
ACR-522ece22a00446179ae6a182e3272dc2
ACR-0f9553d4bd4844e8b83bf7e4eff6cfa8
ACR-c014640a3686404f880139fc79a14591
ACR-939d7252c5ec451593c5bf43565f8f06
ACR-a85e5bbebbab48d5901c7cac51c525a2
ACR-77530a4bf25c48778b47dc817062de18
ACR-d4e2adfad252490cbb545cd14fec2fc9
ACR-551666f927ef4de688aa8b9a2fa02a95
ACR-0fd4774f74fc4efeb253962f0c823433
ACR-38e41fe2c80e42ddb055de82faf6c6c3
ACR-30824a1bb3e5406dbc1deff1bc89ef3b
ACR-559e059bffd84d33af4c6b409c015af1
ACR-83c103a02b054dc691a3c204fd7765dc
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.Objects;
import java.util.function.Function;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/*
ACR-b4422e5c849442f6a7cde7112d07bf7e
ACR-6a6b2c1ea17540e98cc644789e6a33b5
ACR-19b872d35cb14e1c923636ab84f1de7f
 */
public class Either<L, R> {

  private final org.eclipse.lsp4j.jsonrpc.messages.Either<L, R> lsp4jEither;

  public Either(org.eclipse.lsp4j.jsonrpc.messages.Either<L, R> lsp4jEither) {
    this.lsp4jEither = lsp4jEither;
  }

  public static <L, R> Either<L, R> forLeft(@NonNull L left) {
    return new Either<>(org.eclipse.lsp4j.jsonrpc.messages.Either.forLeft(left));
  }

  public static <L, R> Either<L, R> forRight(@NonNull R right) {
    return new Either<>(org.eclipse.lsp4j.jsonrpc.messages.Either.forRight(right));
  }

  public boolean isLeft() {
    return lsp4jEither.isLeft();
  }

  public boolean isRight() {
    return lsp4jEither.isRight();
  }

  public L getLeft() {
    return lsp4jEither.getLeft();
  }

  public R getRight() {
    return lsp4jEither.getRight();
  }

  public <T> T map(
    @NonNull Function<? super L, ? extends T> mapLeft,
    @NonNull Function<? super R, ? extends T> mapRight) {
    return lsp4jEither.map(mapLeft, mapRight);
  }

  public <R1, R2> Either<R1, R2> mapToEither(
    @NonNull Function<? super L, ? extends R1> mapLeft,
    @NonNull Function<? super R, ? extends R2> mapRight) {
    return isLeft() ? Either.forLeft(mapLeft.apply(getLeft())) : Either.forRight(mapRight.apply(getRight()));
  }

  public org.eclipse.lsp4j.jsonrpc.messages.Either<L, R> getLsp4jEither() {
    return lsp4jEither;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Either<?, ?> either = (Either<?, ?>) o;
    return Objects.equals(lsp4jEither, either.lsp4jEither);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lsp4jEither);
  }

  @Override
  public String toString() {
    return lsp4jEither.toString();
  }
}
