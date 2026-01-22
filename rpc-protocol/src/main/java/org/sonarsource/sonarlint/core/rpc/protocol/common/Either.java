/*
ACR-1690ced7593f4a7db39c64b20a833f84
ACR-10d3191889214312aaef6555b9073369
ACR-65e75cdf13d840c689a8d6b39485ce22
ACR-71710b049eda4f5f95ad8a5dc5b87a03
ACR-d8f8c0df996046639a922fccd7b3e10a
ACR-8014dc2479114a7e9efa78b85071e75d
ACR-0539609e809c426db74d5dcdf0bd1de2
ACR-a7f4082df3e9463b800c6ca00643450c
ACR-21af8143e83c49d6973ba5a119105033
ACR-843cd55928944a41a0d7be8986ed12c1
ACR-77f96fe5b1474ce89d980d693e92b1b0
ACR-083595b2ed83424788f62216ae910029
ACR-b8a173374d3f45649a191904f4f10b34
ACR-b016a93428ce4c809777607f0212d1d7
ACR-3a2caa8ca5804b33978d260845e4dc19
ACR-ede6fa94e738474ca97c133f5ca0e220
ACR-93c20b8ac1e245838fa3dbfa28af4bc5
 */
package org.sonarsource.sonarlint.core.rpc.protocol.common;

import java.util.Objects;
import java.util.function.Function;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;

/*
ACR-2277b2013b6a43fe87627d91f01ebeb4
ACR-d032023dc4d541b981dd66db80588578
ACR-bdbc565b4d824a10a4c54b3857c390fa
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
