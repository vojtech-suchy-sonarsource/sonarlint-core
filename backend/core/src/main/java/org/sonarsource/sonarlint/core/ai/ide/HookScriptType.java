/*
ACR-e50682a23e9444fb93d9dc0eccf5bb0f
ACR-9604054c4ef144299f1c5651b0d98251
ACR-8180254a7b5e43849ce1f5efab61a475
ACR-b522e7dd500b4ff08f54820aa2446e2d
ACR-c6ed73e4798b4a94a05152b82e18d279
ACR-da2580f8a32144b088e111b8a86469ad
ACR-033ebec0aeaa421eb44c06795bafc012
ACR-bc21a5d39aea40e08c76991a48d98340
ACR-21ab71a1a3184579b1743a4ea7074780
ACR-40c6181d02964036a4f93475ac420a41
ACR-cbfa0104be2444fca30eacaf04dbef99
ACR-d47268a73b26439cb02e86f05077e15b
ACR-02b7136990b54bb7b82c73415f881b60
ACR-d07d259b7fd6471bb6a1e5e514d5ac25
ACR-2de7a5200b6743448ed7b38af64faaf0
ACR-b5b3e1d68ff8434f9ec274b2fef4ed38
ACR-b9495b4336ae4b43b9fd3f549e56121d
 */
package org.sonarsource.sonarlint.core.ai.ide;

public enum HookScriptType {
  NODEJS("sonarqube_analysis_hook.js"),
  PYTHON("sonarqube_analysis_hook.py"),
  BASH("sonarqube_analysis_hook.sh");

  private final String fileName;

  HookScriptType(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }
}

