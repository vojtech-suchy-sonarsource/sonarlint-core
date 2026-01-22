/*
ACR-1571bf5c30794faeb75d83af2125315e
ACR-c0c96b361980424185eebe054e06cab5
ACR-a50e9bb15e3d42339ae3fc66412d456d
ACR-c5a1d0d704434fc6a1751956732a3d7c
ACR-4010c97ae9124c8282442392ca8a2cbf
ACR-ac837a972d3c4c20939c3e9381d9bc4f
ACR-1fd336e4b53348518e79db1f7062e431
ACR-61c5b26f40de4ba2a7eaa4c2d5031d5f
ACR-69a1c6545bbb41ee839bb01c81991042
ACR-3219e8e6c0154c09b5c9b0988f25e7d1
ACR-074616a3cf764db3bb53a79bc87bd606
ACR-613d27bcbd364fefa895a45f9bafcd99
ACR-f4262ff1e9f44b819e8071640c3ed116
ACR-0d2b754781234a6b82e530972dc456cf
ACR-84865e86b2a9492b9d7fc68f7b723d27
ACR-f98a60dc598442879a6e378b75391af4
ACR-2d4b148b7c1546658fb3a0567a1ae14e
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
