/*
ACR-e9e350cfb6894326ace70636133479f0
ACR-a77321186f3f4deabd9217049e5c44c0
ACR-0bfda5ead0114a31b74bd4a17176f8a7
ACR-c2448a1bf9124f53b1cb4d96a7126beb
ACR-8a1558ce1e2f4f7d8aef0269b0bdccd5
ACR-df944c543e7643809eaea4b19ec4681f
ACR-81b404f183c444d587500befbc595425
ACR-a3279a44a0ea47cba2552b53cb9a89c7
ACR-c1a38f9bab23431484486f6c75d3d93d
ACR-267a6cd89a324656a579e720802e1170
ACR-7143dcf0af50479b85a2e15c4f8b76ea
ACR-897c3cbd072b4c20b72f1b2d8a8538b3
ACR-33b70519ee5c4eeaac2debd6cd5275fe
ACR-d6aec1b61320408595fd6be21a4e4545
ACR-91117bfcc2fd4c33a5839b7b20f36af7
ACR-da8e91a73e114d79a7a42fc54299d1da
ACR-f53f4786d9ae450ebaaca3b111292db9
 */
package org.sonarsource.sonarlint.core.client.utils;

public enum Language {

  ABAP("ABAP"),
  APEX("Apex"),
  C("C"),
  CPP("C++"),
  CS("C#"),
  CSS("CSS"),
  OBJC("Objective-C"),
  COBOL("COBOL"),
  HTML("HTML"),
  IPYTHON("IPython Notebooks"),
  JAVA("Java"),
  JCL("JCL"),
  JS("JavaScript"),
  KOTLIN("Kotlin"),
  PHP("PHP"),
  PLI("PL/I"),
  PLSQL("PL/SQL"),
  PYTHON("Python"),
  RPG("RPG"),
  RUBY("Ruby"),
  SCALA("Scala"),
  SECRETS("Secrets"),
  TEXT("Text"),
  SWIFT("Swift"),
  TSQL("T-SQL"),
  TS("TypeScript"),
  JSP("JSP"),
  VBNET("VB.NET"),
  XML("XML"),
  YAML("YAML"),
  JSON("JSON"),
  GO("Go"),
  CLOUDFORMATION("CloudFormation"),
  DOCKER("Docker"),
  KUBERNETES("Kubernetes"),
  TERRAFORM("Terraform"),
  AZURERESOURCEMANAGER("AzureResourceManager"),
  ANSIBLE("Ansible"),
  GITHUBACTIONS("GitHub Actions");
  private final String label;

  Language(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public static Language fromDto(org.sonarsource.sonarlint.core.rpc.protocol.common.Language rpcEnum) {
    switch (rpcEnum) {
      case ABAP:
        return ABAP;
      case APEX:
        return APEX;
      case C:
        return C;
      case CPP:
        return CPP;
      case CS:
        return CS;
      case CSS:
        return CSS;
      case OBJC:
        return OBJC;
      case COBOL:
        return COBOL;
      case GITHUBACTIONS:
        return GITHUBACTIONS;
      case HTML:
        return HTML;
      case IPYTHON:
        return IPYTHON;
      case JAVA:
        return JAVA;
      case JCL:
        return JCL;
      case JS:
        return JS;
      case KOTLIN:
        return KOTLIN;
      case PHP:
        return PHP;
      case PLI:
        return PLI;
      case PLSQL:
        return PLSQL;
      case PYTHON:
        return PYTHON;
      case RPG:
        return RPG;
      case RUBY:
        return RUBY;
      case SCALA:
        return SCALA;
      case SECRETS:
        return SECRETS;
      case TEXT:
        return TEXT;
      case SWIFT:
        return SWIFT;
      case TSQL:
        return TSQL;
      case TS:
        return TS;
      case JSP:
        return JSP;
      case VBNET:
        return VBNET;
      case XML:
        return XML;
      case YAML:
        return YAML;
      case JSON:
        return JSON;
      case GO:
        return GO;
      case CLOUDFORMATION:
        return CLOUDFORMATION;
      case DOCKER:
        return DOCKER;
      case KUBERNETES:
        return KUBERNETES;
      case TERRAFORM:
        return TERRAFORM;
      case AZURERESOURCEMANAGER:
        return AZURERESOURCEMANAGER;
      case ANSIBLE:
        return ANSIBLE;
      default:
        throw new IllegalArgumentException("Unknown language: " + rpcEnum);
    }
  }

}
