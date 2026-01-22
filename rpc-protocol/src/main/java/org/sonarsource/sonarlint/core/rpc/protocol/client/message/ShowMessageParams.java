/*
ACR-895012edc0a74acdbba72f5e0ce388f1
ACR-b10c7c2d3e384cbea261f41a31045a67
ACR-dbd33a6f96504d9e81e34af66461f0d1
ACR-85176dfc6c4b4daeb3c28fbac7d7bfd8
ACR-8dce4ca12d8b42409503050e7b38f671
ACR-656fa7e109bf4eb190ddd59b02ab861d
ACR-f1f2316fc0bc46e7a39023361a58859c
ACR-337f9ef3e00844268697613b2c8f1f1a
ACR-2223805f843143f3b1ad997be15dcfe3
ACR-11310299b1e448c5aaaa8e501e15f1e1
ACR-eae5b720ee0d4673b9a8f179723a94c2
ACR-e118226ae8e74c7983ce284598bfe383
ACR-739fb58e31e842e69472f59b53d8da3a
ACR-9c21b85f84d0433c884879e0cb076c3a
ACR-4d82c95ef8564ad181cbfdab1bcc4d2f
ACR-98995ab32856423593ad4e35514a6f48
ACR-9b5dce31bc1643fdad31422aba8417a8
 */
package org.sonarsource.sonarlint.core.rpc.protocol.client.message;

/*ACR-25052516461a4cb2a5391c37d481dedd
ACR-b44b68b605bd4a1ca415445e5d172376
ACR-1b79702b049d4f20a474c86e2b97c3db
ACR-5f0e6e9033414db3ac2a2be9ac0a6e0a
ACR-f46281d7f1944f478e3d8057c5e9549d
ACR-f4044fdf204643bb97e2889626041ef2
ACR-2c5c275d278c4f90908b4a8bc6f6c8b3
 */
public class ShowMessageParams {
  private final MessageType type;
  private final String text;

  public ShowMessageParams(MessageType type, String text) {
    this.type = type;
    this.text = text;
  }

  public MessageType getType() {
    return type;
  }

  public String getText() {
    return text;
  }
}
