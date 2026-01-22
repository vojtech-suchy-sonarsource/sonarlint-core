/*
ACR-3d6a2fe44f28400d89e2031a549d8be9
ACR-c17b640b9e1c4a8ba6b84fd75b4c7a93
ACR-f91e592dafd7434fa32e8b13d47cdb5e
ACR-7b2ca2d359104dbba291a6305b3592d9
ACR-babf520a983f40508260896d5fb77a9b
ACR-8165187dd5d14e6fa9124eca71ddc67e
ACR-3ee0e2ae86aa4b7d9f8c741854e610e7
ACR-f1787271be4d450d892496579ea935bd
ACR-481ce4d617fc40b2bb47810cc3e991a9
ACR-531d8ccb4d004e95a82b2984b1560742
ACR-2000bad6268f4328a471e85578bc1fc6
ACR-2343e4a1b32146b59d22b3cc84ccfd5c
ACR-109f1127bd9f49839ce1d28e9fc89138
ACR-5480e65fe03c4e369f9b06906ea9075b
ACR-3934a07c7ebf444d83dcaa7b2b24a352
ACR-8e42e4f1279e4a27a6965e6aabc2c913
ACR-e85f946d886b40c2baaae81be3129c17
 */
package org.sonarsource.sonarlint.core.rpc.protocol;

public class SonarLintRpcErrorCode {

  public static final int CONNECTION_NOT_FOUND = -1;
  public static final int CONFIG_SCOPE_NOT_FOUND = -2;
  public static final int RULE_NOT_FOUND = -3;
  public static final int BACKEND_ALREADY_INITIALIZED = -4;
  public static final int ISSUE_NOT_FOUND = -5;
  public static final int CONFIG_SCOPE_NOT_BOUND = -6;
  public static final int HTTP_REQUEST_TIMEOUT = -7;
  public static final int HTTP_REQUEST_FAILED = -8;
  public static final int TASK_EXECUTION_TIMEOUT = -9;
  public static final int PROGRESS_CREATION_FAILED = -10;
  public static final int CONNECTION_KIND_NOT_SUPPORTED = -11;
  public static final int FILE_NOT_FOUND = -12;
  public static final int TOO_MANY_REQUESTS = -13;
  public static final int UNAUTHORIZED = -14;
  public static final int INVALID_ARGUMENT = -15;
}
