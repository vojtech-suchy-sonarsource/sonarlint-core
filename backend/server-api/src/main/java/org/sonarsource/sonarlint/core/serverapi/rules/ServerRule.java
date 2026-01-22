/*
ACR-df8a00f2bcd543ddb429bb629951e9e7
ACR-80d36dd94d0140e1b4807f53ff7d0895
ACR-9ba9357d8761407d900e56a3af71e7a8
ACR-7337fd03d0394a6597d6e03fb3dd105f
ACR-6b37edeae3284dd097e89bf2aba091e4
ACR-6f3245f3d40744778832b05c21487e40
ACR-05c8ab2b2f9e45feaafc1f50100259a5
ACR-740ca89c6dba48808734365e7940e26a
ACR-07843596df68433f9e631ba00ab992ba
ACR-2b54b7eac5b548b38a442f101abc8678
ACR-8000c23dd5424563a47d4394cc63611c
ACR-4cb7946b00cd4128946bc70f5ec89935
ACR-2fcc0fa8d42c48d6ad994222ae0ffe5e
ACR-7215124fa3dd483986da76f6f28b9fa1
ACR-1631810683d8467d97bef1fe00660ea0
ACR-44f702ec618b41d0a426b4964b67bac5
ACR-8049d2560ffc4254b604e6784e87dc3a
 */
package org.sonarsource.sonarlint.core.serverapi.rules;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonarsource.sonarlint.core.commons.CleanCodeAttribute;
import org.sonarsource.sonarlint.core.commons.ImpactSeverity;
import org.sonarsource.sonarlint.core.commons.IssueSeverity;
import org.sonarsource.sonarlint.core.commons.RuleType;
import org.sonarsource.sonarlint.core.commons.SoftwareQuality;
import org.sonarsource.sonarlint.core.commons.api.SonarLanguage;

public class ServerRule {
  private final String name;
  private final String htmlDesc;
  private final List<DescriptionSection> descriptionSections;
  private final String htmlNote;
  private final IssueSeverity severity;
  private final RuleType type;
  private final SonarLanguage language;
  private final Set<String> educationPrincipleKeys;
  private final CleanCodeAttribute cleanCodeAttribute;
  private final Map<SoftwareQuality, ImpactSeverity> impacts;


  public ServerRule(String name, IssueSeverity severity, RuleType type, String language, String htmlDesc, List<DescriptionSection> descriptionSections, String htmlNote,
    Set<String> educationPrincipleKeys, @Nullable CleanCodeAttribute cleanCodeAttribute, Map<SoftwareQuality, ImpactSeverity> impacts) {
    this.name = name;
    this.severity = severity;
    this.type = type;
    this.language = SonarLanguage.forKey(language).orElseThrow(() -> new IllegalArgumentException("Unknown language with key: " + language));
    this.htmlDesc = htmlDesc;
    this.descriptionSections = descriptionSections;
    this.htmlNote = htmlNote;
    this.educationPrincipleKeys = educationPrincipleKeys;
    this.cleanCodeAttribute = cleanCodeAttribute;
    this.impacts = impacts;
  }

  public String getName() {
    return name;
  }

  public String getHtmlDesc() {
    return htmlDesc;
  }

  public List<DescriptionSection> getDescriptionSections() {
    return descriptionSections;
  }

  public String getHtmlNote() {
    return htmlNote;
  }

  public IssueSeverity getSeverity() {
    return severity;
  }

  public RuleType getType() {
    return type;
  }

  public SonarLanguage getLanguage() {
    return language;
  }

  public Set<String> getEducationPrincipleKeys() {
    return educationPrincipleKeys;
  }

  @CheckForNull
  public CleanCodeAttribute getCleanCodeAttribute() {
    return cleanCodeAttribute;
  }

  public Map<SoftwareQuality, ImpactSeverity> getImpacts() {
    return impacts;
  }

  public static class DescriptionSection {
    private final String key;
    private final String htmlContent;
    private final Optional<Context> context;

    public DescriptionSection(String key, String htmlContent, Optional<Context> context) {
      this.key = key;
      this.htmlContent = htmlContent;
      this.context = context;
    }

    public String getKey() {
      return key;
    }

    public String getHtmlContent() {
      return htmlContent;
    }

    public Optional<Context> getContext() {
      return context;
    }

    public static class Context {
      private final String key;
      private final String displayName;

      public Context(String key, String displayName) {
        this.key = key;
        this.displayName = displayName;
      }

      public String getKey() {
        return key;
      }

      public String getDisplayName() {
        return displayName;
      }
    }
  }
}
