/*
ACR-ab5063485b2949218cd0f823209822a4
ACR-551b1124574844f4bf9a60cbe59ea7f3
ACR-79b7b15f5cc04a00b81594ec9e2d4814
ACR-fd29b1dd16ac4bda9e8a950511c208d3
ACR-6f36a88545804c6f8d3d047a93d59db4
ACR-5b04b41ce92b4ef9ac60322fad9efd8d
ACR-923a326e574d4f2887262ef6104c838d
ACR-5a1aaeb951354146b90dba8f88e5ea78
ACR-50796edec350410098e8f8fc64e51ca0
ACR-4eda7578b0dc403daed99baf81bc08fa
ACR-ce2863db0aee4d1fa7e1a7d89c8e0ad0
ACR-971c4c850ba54f9a976d199dcc4081ff
ACR-f328488a19ba413195914ef03b29f82c
ACR-0d0dffcf687a4b97b29e1e4f4b7988a7
ACR-2777c86f53474ddb8e50e279111297d2
ACR-d6000f4414354d62a8ea74184592e930
ACR-388303566ed94b99961c4ba5ba4d0150
 */
package org.sonarsource.sonarlint.core.analysis.api;

import javax.annotation.CheckForNull;

public interface IssueLocation extends WithTextRange {

  @CheckForNull
  String getMessage();

  /*ACR-de012c71313843ddb6550879dc95c93d
ACR-cf09c7a46c0b448699217e6ac476209e
   */
  @CheckForNull
  ClientInputFile getInputFile();
}
