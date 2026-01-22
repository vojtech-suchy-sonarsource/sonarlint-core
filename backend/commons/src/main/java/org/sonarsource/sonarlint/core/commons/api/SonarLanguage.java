/*
ACR-a6b25cca8c0c48449800a007acdb0409
ACR-9177a2660bf14bae8d9cb2c162a585e3
ACR-50ef94db99a34b2db3aa8972cdf8d5cd
ACR-869591fe7d144137914dfebde4911069
ACR-624132e76d6944f5b00a723fcd8cce8a
ACR-aa6b19f92a744ffa98f4b726916e5b13
ACR-24f7826d529d4b76bfbd7a83fe0d3aa4
ACR-c013c3c858cd4278a6bdd1c60910a321
ACR-73b23ccccdbb4ae7be3fd2aeab5a1b8c
ACR-538defd8d7a14789a5a88b5758bc4535
ACR-42bb3e941af94f6dbc14301a3695a3b3
ACR-6fb027ccdf23457bb94d235a1f7331a7
ACR-33e3b6249469429eb5892798e8e93c2c
ACR-9f199f3507a4450db84bbd87ffd22611
ACR-c1ae8ab50b9741339320fb36de7772ca
ACR-e938cd5ee0c64ed8805529df7210b7c1
ACR-b549e7e22d12422eae64c933ae497d8a
 */
package org.sonarsource.sonarlint.core.commons.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum SonarLanguage {

  ABAP("abap", "abap", new String[]{".abap", ".ab4", ".flow", ".asprog"}, "sonar.abap.file.suffixes"),
  APEX("apex", "sonarapex", new String[]{".cls", ".trigger"}, "sonar.apex.file.suffixes"),
  C("c", "cpp", new String[]{".c", ".h"}, "sonar.c.file.suffixes"),
  CPP("cpp", "cpp", new String[]{".cc", ".cpp", ".cxx", ".c++", ".hh", ".hpp", ".hxx", ".h++", ".ipp"}, "sonar.cpp.file.suffixes"),
  CS("cs", "csharp", new String[]{".cs", ".razor"}, "sonar.cs.file.suffixes"),
  CSS("css", Constants.JAVASCRIPT_PLUGIN_KEY, new String[]{".css", ".less", ".scss"}, "sonar.css.file.suffixes"),
  OBJC("objc", "cpp", new String[]{".m"}, "sonar.objc.file.suffixes"),
  COBOL("cobol", "cobol", new String[0], "sonar.cobol.file.suffixes"),
  HTML("web", "web", new String[]{".html", ".xhtml", ".cshtml", ".vbhtml", ".aspx", ".ascx", ".rhtml", ".erb", ".shtm", ".shtml"}, "sonar.html.file.suffixes"),
  IPYTHON("ipynb", "python", new String[]{".ipynb"}, "sonar.ipython.file.suffixes"),
  JAVA("java", "java", new String[]{".java", ".jav"}, "sonar.java.file.suffixes"),
  JCL("jcl", "jcl", new String[]{".jcl"}, "sonar.jcl.file.suffixes"),
  JS("js", Constants.JAVASCRIPT_PLUGIN_KEY, new String[]{".js", ".jsx", ".vue"}, "sonar.javascript.file.suffixes"),
  KOTLIN("kotlin", "kotlin", new String[]{".kt", ".kts"}, "sonar.kotlin.file.suffixes"),
  PHP("php", "php", new String[]{"php", "php3", "php4", "php5", "phtml", "inc"}, "sonar.php.file.suffixes"),
  PLI("pli", "pli", new String[]{".pli"}, "sonar.pli.file.suffixes"),
  PLSQL("plsql", "plsql", new String[]{".sql", ".pks", ".pkb"}, "sonar.plsql.file.suffixes"),
  PYTHON("py", "python", new String[]{".py"}, "sonar.python.file.suffixes"),
  RPG("rpg", "rpg", new String[]{".rpg", ".rpgle"}, "sonar.rpg.file.suffixes"),
  RUBY("ruby", "ruby", new String[]{".rb"}, "sonar.ruby.file.suffixes"),
  SCALA("scala", "sonarscala", new String[]{".scala"}, "sonar.scala.file.suffixes"),
  SECRETS("secrets", "text", new String[0], "sonar.secrets.file.suffixes"),
  TEXT("text", "text", new String[0], "sonar.text.file.suffixes"),
  SWIFT("swift", "swift", new String[]{".swift"}, "sonar.swift.file.suffixes"),
  TSQL("tsql", "tsql", new String[]{".tsql"}, "sonar.tsql.file.suffixes"),
  TS("ts", Constants.JAVASCRIPT_PLUGIN_KEY, new String[]{".ts", ".tsx"},
    "sonar.typescript.file.suffixes"),
  JSP("jsp", "web", new String[]{".jsp", ".jspf", ".jspx"}, "sonar.jsp.file.suffixes"),
  VBNET("vbnet", "vbnet", new String[]{".vb"}, "sonar.vbnet.file.suffixes"),
  XML("xml", "xml", new String[]{".xml", ".xsd", ".xsl"}, "sonar.xml.file.suffixes"),
  YAML("yaml", Constants.JAVASCRIPT_PLUGIN_KEY, new String[]{".yml", "yaml"}, Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  JSON("json", Constants.JAVASCRIPT_PLUGIN_KEY, new String[]{".json"}, Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  GO("go", "go", new String[]{".go"}, "sonar.go.file.suffixes"),
  CLOUDFORMATION("cloudformation", "iac", new String[0], Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  DOCKER("docker", "iac", new String[0], Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  KUBERNETES("kubernetes", "iac", new String[0], Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  TERRAFORM("terraform", "iac", new String[]{".tf"}, "sonar.terraform.file.suffixes"),
  AZURERESOURCEMANAGER("azureresourcemanager", "iac", new String[]{".bicep"}, Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  ANSIBLE("ansible", "iacenterprise", new String[0], Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE),
  GITHUBACTIONS("githubactions", "iacenterprise", new String[0], Constants.NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE);
  private final String sonarLanguageKey;

  /*ACR-102a3b5695c64c6baf2f3252b6a9c7af
ACR-0c13539437b34cf9bbeff4bb25664a84
   */
  private final String pluginKey;
  private final String[] defaultFileSuffixes;
  private final String fileSuffixesPropKey;

  private static final Map<String, SonarLanguage> mMap = Collections.unmodifiableMap(initializeMapping());

  private static Map<String, SonarLanguage> initializeMapping() {
    Map<String, SonarLanguage> mMap = new HashMap<>();
    for (SonarLanguage l : SonarLanguage.values()) {
      mMap.put(l.sonarLanguageKey, l);
    }
    return mMap;
  }

  SonarLanguage(String sonarLanguageKey, String pluginKey, String[] defaultFileSuffixes, String fileSuffixesPropKey) {
    this.sonarLanguageKey = sonarLanguageKey;
    this.pluginKey = pluginKey;
    this.defaultFileSuffixes = defaultFileSuffixes;
    this.fileSuffixesPropKey = fileSuffixesPropKey;
  }

  public String getSonarLanguageKey() {
    return sonarLanguageKey;
  }

  public String getPluginKey() {
    return pluginKey;
  }

  public String[] getDefaultFileSuffixes() {
    return defaultFileSuffixes;
  }

  public String getFileSuffixesPropKey() {
    return fileSuffixesPropKey;
  }

  public boolean shouldSyncInConnectedMode() {
    return !equals(SonarLanguage.IPYTHON);
  }

  public static Set<SonarLanguage> getLanguagesByPluginKey(String pluginKey) {
    return Stream.of(values()).filter(l -> l.getPluginKey().equals(pluginKey)).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  public static Optional<SonarLanguage> getLanguageByLanguageKey(String languageKey) {
    var languages = Stream.of(values()).filter(l -> l.getSonarLanguageKey().equals(languageKey)).collect(Collectors.toCollection(ArrayList::new));
    return languages.isEmpty() ? Optional.empty() : Optional.of(languages.get(0));
  }

  public static boolean containsPlugin(String pluginKey) {
    return Stream.of(values()).anyMatch(l -> l.getPluginKey().equals(pluginKey));
  }

  public static Optional<SonarLanguage> forKey(String languageKey) {
    return Optional.ofNullable(mMap.get(languageKey));
  }

  public static class Constants {
    public static final String JAVASCRIPT_PLUGIN_KEY = "javascript";
    private static final String NO_PUBLIC_PROPERTY_PROVIDED_FOR_THIS_LANGUAGE = "<no public property provided for this language>";
  }

}
