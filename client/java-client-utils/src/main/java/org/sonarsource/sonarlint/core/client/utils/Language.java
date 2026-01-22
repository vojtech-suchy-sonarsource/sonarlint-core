/*
ACR-103da679d14c4825a5f93bbe91795e40
ACR-e4160ba009c2483cb4d3488b72af36d9
ACR-f76a6dc3a5414d179b56e881937ca84b
ACR-4711d10d06fb4ef08ec59c1deb6e84e4
ACR-865563f9cf2746058b2189ace3005baf
ACR-f5f638e22ae74d949773acbdcd2866de
ACR-31430ace733141659436be3c0e520c4a
ACR-df2b1aa8a6a64b349ff53e4259a397e9
ACR-40a7e8c22f1d4e20b9adb44f6bad81eb
ACR-0909258ba410472ea5dac8704fd70690
ACR-b411b7b3b3bc4b41944e2e980c083f13
ACR-d95580a3eb0e4a418574ec377f09e932
ACR-36a28276d630458b92cdddd0be119ece
ACR-939655e99b12424ba951376326994d68
ACR-d764cf0a4e8748e3b3a1308552c9872d
ACR-b67af7d4bf7f474c93be765797c4d27f
ACR-3b1721531f08446c9d7174c142ff1d57
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
