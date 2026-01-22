/*
ACR-0789a9054a3a4e2e8c0b44206f402180
ACR-c9d5517d0d924ff58dedbfdbc46e4e77
ACR-85f141e09d05445f918c9093837407bc
ACR-5c2d217069c540b1b7646f2ccafd98e8
ACR-93ee021629a24c17adf8a4df25c3c6cc
ACR-4a37251876f14a549bbbfa663e11286a
ACR-de827474e1774b01a0010efcf6e2a1d4
ACR-fa198d95142a495c9a8f780803172a30
ACR-0543cd1af42f42638d43a35a28a8bcb7
ACR-43c8c4fb527a49bd9ddf6641a121a5fa
ACR-74838a581f444bd4ae83b5d3693651eb
ACR-c7ba8dbd175a413bbc516b6dcf3214f4
ACR-e5a4f414e9b04f8888815e043ab2ee28
ACR-18a0d798698548258440e831e22e5285
ACR-4eeee776f2714137bccb9e811189c7e8
ACR-8dacee0a507f459f839743da99a1ee0d
ACR-a502b7d0c9c1414392d7b3bfe180df42
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

  /*ACR-c8b4b08027f64d9aa0969faea951548c
ACR-e40ca78804484949bc79744894d506ea
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
