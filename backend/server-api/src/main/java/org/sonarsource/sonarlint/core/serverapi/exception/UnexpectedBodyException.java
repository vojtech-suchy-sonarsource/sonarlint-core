/*
ACR-7fcd012aae764511af2d27df94e177ee
ACR-a0f8c5ee2c5e48e6b2ae982e17716312
ACR-ce76874ec51d4a84b8375ddfcdbd1c8e
ACR-a540def53f5b48a780070418df82540a
ACR-e00c474bfe3041068abae015d774ba24
ACR-191b3320cc8941f7a748075f29719cb8
ACR-df08e702469b4b8ea09aa474b87ccfbf
ACR-b28aa9e91e444959935fb9878d884362
ACR-baa1b7bf7b02466daf3850ebee0b64fa
ACR-8dbf6d3bbc50449f9a30b5badf897c4d
ACR-61fc1be9ece649fdaa045dd22876fdc4
ACR-bce5d8a67bb24324ab3a4cc3c48afb05
ACR-b504c6fec27f4deca543cc3292bbd2a7
ACR-726e036dbbff4e3fbebaa5e7287b0ad9
ACR-483b40588c72435ca4f81cfcdf43035f
ACR-ebdf83b52be34c82b2af2033ad686547
ACR-b113099b1bf046a993571c9491273869
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
