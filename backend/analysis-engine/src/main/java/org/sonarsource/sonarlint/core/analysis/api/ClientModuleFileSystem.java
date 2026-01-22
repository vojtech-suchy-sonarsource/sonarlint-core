/*
ACR-c431a70ca5ed4154a8ea97917388002a
ACR-a61e3a2bd54d4ac2b2c66bd5f2a46778
ACR-92cd69e71d07406782b33c27dd30ffb5
ACR-56a8dda58eeb4ef799b9721fd4cc3124
ACR-ce2a3c27287243cbb18db7db6864952e
ACR-0082a282f6fe4f38b4b0844d91c5d459
ACR-b6b5d299f20e4ec199b87e5bb38164aa
ACR-78093c5a767c4e03b83a1213d066f0c2
ACR-e5e2eec59f2f4f0299cddc1d850d8897
ACR-131bfceac11347878ecc942844e376fa
ACR-ed09833478cc4694b43b8240c8bc0f81
ACR-f857f29ca18d499e8c07893d3e86863f
ACR-73f100a25bce466681e0af0f608670e6
ACR-2802bc23cd14401bac1c78946676c779
ACR-c3023e52f7dd4cda8b5d9b61f36d43f0
ACR-de5694351d1e4cd38863447bca16e3b6
ACR-e615f79d22484bf5bafd3eef67310e03
 */
package org.sonarsource.sonarlint.core.analysis.api;

import java.util.stream.Stream;
import org.sonar.api.batch.fs.InputFile;

public interface ClientModuleFileSystem {
  Stream<ClientInputFile> files(String suffix, InputFile.Type type);
  Stream<ClientInputFile> files();
}
