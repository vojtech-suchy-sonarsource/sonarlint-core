/*
ACR-0705495462894553bc8c8c7988b3b392
ACR-5ed79f1829104dfd9f0e1515fd3d7462
ACR-d492c70d5a164e73816ebba0985c0b48
ACR-41b68a88fe3c4728b55f03504bda395e
ACR-f411b12057544792a5cc3c767b03793f
ACR-411031fd06824a4d9cd6fac9bbf5a44f
ACR-3cffadd041df4e3097a6478af1e5c334
ACR-232f0cfacc744614abe90edf61c08a2a
ACR-3db99dda77284581941f93f1690a5f4f
ACR-dca08e9e6900454a995d0f35f2126f8b
ACR-cef4ecebc7fe430b9f93c57d9ca80f7f
ACR-d412656cf0c9411ba4f51f2f1447746a
ACR-0eb338c5d4924d2d930c284871448c31
ACR-c5f3a5754ca9435d8da5c4c19db5f780
ACR-0ac1c5ffa82d46d19f620a6d00b808f2
ACR-14d14bd695da4099920759b21011a62a
ACR-6cc357ffb851447ba5767b6879cef991
 */
package org.sonarsource.sonarlint.core.serverapi.exception;

public class UnexpectedBodyException extends ServerRequestException {
  public UnexpectedBodyException(Throwable cause) {
    super("Unexpected body received", cause);
  }

  public UnexpectedBodyException(String message) {
    super(message);
  }
}
