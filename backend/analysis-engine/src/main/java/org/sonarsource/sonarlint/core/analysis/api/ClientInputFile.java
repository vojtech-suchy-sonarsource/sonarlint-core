/*
ACR-169adc3dfe1d43afb9eda5aae3023802
ACR-fd61590c45c54fd2891b5dbb86e504b9
ACR-bee3471c35db40b6bc7d0c81c10f1b44
ACR-5f1a77ecac7a4be59addd205da463c24
ACR-89011cb56d944324bc72655d546e10aa
ACR-b922ed2f184b42789c2158769abf8308
ACR-76bef7b25c4646a4813b97a5be495de5
ACR-ba290162b8ac48cb901b5531706d1bf6
ACR-a387e18c7f794838a34f46762e5f9399
ACR-e97a06cdaa5148dd90ba2c729a1c93ae
ACR-451c28074e644ba086fd7f79decfb145
ACR-a348a53990374c03abffe0e826c0fe7e
ACR-8ffaee31cfd441dea230a2bae336fdd0
ACR-55d42a2523c947b4896f74c0abae59e7
ACR-7ba8125d813b49dfa8729ac663c20027
ACR-7394b60013284a939836187eb8d7a051
ACR-b21619cefa0e4fa0808e35d2974265a4
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

/*ACR-8ce56d73624b4e2289fe255def996f11
ACR-72a4e0f4de2e4526828056262341ff6f
ACR-4c63bcb0f02243d2be9da14940506614
 */
public interface ClientInputFile {

  /*ACR-7febc607ac804b929ae3273e05b53ddf
ACR-0b13ea31c29241d9865c3a860a93062f
ACR-da40df7674cf4f859d4ae7a5240ea0e4
ACR-0f12ebf4420e47eea43ae992b0e53bdb
   */
  @Deprecated
  String getPath();

  /*ACR-a00cc0cd60ee4a278ea05df63e9c6424
ACR-beef31a8bf1f4f398f4c272a0cbfed1a
   */
  boolean isTest();

  /*ACR-d308025918d144a186e884474856c766
ACR-4e812a409a9c45aea21e3dda7f5c9653
   */
  @CheckForNull
  Charset getCharset();

  /*ACR-15550ad829664c5fbdbdd64bc8eb5339
ACR-6c780ad5f2a343b8b12a1508cffaa52b
ACR-ca1f9af321544a03a11010ccfdbf7834
   */
  @CheckForNull
  default SonarLanguage language() {
    return null;
  }

  /*ACR-6b64af2b299c4676a4575d3328740a90
ACR-3977278cce894ca3b0e29a12c9be8e57
   */
  <G> G getClientObject();

  /*ACR-a5948ebe4cb041888a57c73262beba11
ACR-4d333542867447f283b802d5cb95e893
   */
  InputStream inputStream() throws IOException;

  /*ACR-da6ee3ab41f5402fa56131528f393f37
ACR-79571f34aa4d4144a6b8b4237205b3c3
   */
  String contents() throws IOException;

  /*ACR-26a3c402a97446179e18b9bb08059009
ACR-f04a20f8124140a8becbec1a194f350f
ACR-ed5f3d1348964363b8d190b7dc8f065d
   */
  String relativePath();

  /*ACR-5520e652e6c347de910fc39ed32c87b2
ACR-daaf92a7d4704323b45772e7fcac1c44
   */
  URI uri();

  default boolean isDirty() {
    return false;
  }
}
