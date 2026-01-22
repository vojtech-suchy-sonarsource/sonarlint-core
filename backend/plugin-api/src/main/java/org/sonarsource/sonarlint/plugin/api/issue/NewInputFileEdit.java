/*
ACR-d2cb90bddea343ebb0b6b970c08de3f6
ACR-02faeacbd6ec475c82982451cb0b5324
ACR-126b029e6d9f4291b4511932b9425a70
ACR-00c34ca0a9a343db962fc7f13efca4b4
ACR-b49aa75edaa14f02a8602bafe75551a1
ACR-52c34ce77ab5475e980ad2df7c09fd4f
ACR-b7e0df53740c409fb4a4f9c98248aa97
ACR-ed012247b6624fc5966946feb5f0aa86
ACR-4a5670645423478992edda9134535d2a
ACR-c4fde86b6cba491384ecab8a12c9105b
ACR-c6e5f5ecf27a48f5b2aba6b5b2a10cb5
ACR-ed905437f04a4d6db9090f16eb3462d9
ACR-e74438541cbb45a5a25aa81b85dfa1fa
ACR-e82175e9de37476dafe2b71d6d4ba6e2
ACR-1580160c835b498687f0b307c9633c9f
ACR-61d9ebee40d44ed09acd1f9c94d4b42c
ACR-aaa41e75d6fe4b61b948e6a373dbb583
 */
package org.sonarsource.sonarlint.plugin.api.issue;

import org.sonar.api.batch.fs.InputFile;

/*ACR-8d1c5dd0027a4fb1ae0e61a8c7f2271e
ACR-37d5b3dd234f45889a467b1e476a008d
ACR-58b8d93b396048da86c255143678195c
ACR-36a4cb5468ed48c5b8c00bfb7a4c232f
ACR-b5cf3fc618bf44c6ba59a3febd6a4123
 */
@Deprecated(since = "8.12")
public interface NewInputFileEdit {

  /*ACR-7d345118ae3a49eda97ce1ba5749e10c
ACR-6cfc88d60afc4cc0aba8f6ae414ec963
ACR-fe25108050d8408ea15fc285d85bd43f
   */
  NewInputFileEdit on(InputFile inputFile);

  /*ACR-9546c21878d94bb8aa4c58c5bc206c70
ACR-6445292ec55f478da7faeed96acfe3f4
ACR-d748ceb086ac4d8a8f186bfe6dffface
   */
  NewTextEdit newTextEdit();

  /*ACR-c06812abf8de470ba2b5e09df7c54276
ACR-0bc788a3eeb84a6180c7fe172ca1099e
ACR-15be89da8e7a448d82e67624d9aadaa1
ACR-5044d68b970b4ac8a5a4c8e82b29170b
   */
  NewInputFileEdit addTextEdit(NewTextEdit newTextEdit);
}
