/*
ACR-7448aeb055c94d4ea5ad606feb118a22
ACR-f1b0e93deb7248ba8f3895b49217783b
ACR-41fb6ae7933b47e1a49f0e732fd53e85
ACR-0f7f9181f0534816b0047bcdba6ff2fe
ACR-026b66ac0f9b4137860fd6a9a934e597
ACR-0dda362d7e4b4cdb9c15022d31260016
ACR-4c4776ac33c54102b2f9be1525b277e5
ACR-d3e71e4dcf3f4c52b43391f4a85654af
ACR-8ba5976448b24c77a34a7836e73e1a77
ACR-4b69a8dbb74e412aad277de2917639f8
ACR-a37de4acfe32490cb30a5b0ab62caa56
ACR-e0dac56cfd584bb8952f5f111deb0b78
ACR-f9d0cfa0ea2f4b1896b52ab8358f6288
ACR-b3a23e9fb750480abae0948cbb076147
ACR-39c381e0b05c4208b5825537784e3567
ACR-395ed240fc4145afb7c9a32dfb5e915d
ACR-926b82f0f62b41549739b7ea389f3757
 */
package org.sonarsource.sonarlint.core.serverapi;

import java.util.Optional;
import org.sonarsource.sonarlint.core.http.HttpClient;
import org.sonarsource.sonarlint.core.serverapi.authentication.AuthenticationApi;
import org.sonarsource.sonarlint.core.serverapi.branches.ProjectBranchesApi;
import org.sonarsource.sonarlint.core.serverapi.component.ComponentApi;
import org.sonarsource.sonarlint.core.serverapi.developers.DevelopersApi;
import org.sonarsource.sonarlint.core.serverapi.features.FeaturesApi;
import org.sonarsource.sonarlint.core.serverapi.fixsuggestions.FixSuggestionsApi;
import org.sonarsource.sonarlint.core.serverapi.hotspot.HotspotApi;
import org.sonarsource.sonarlint.core.serverapi.issue.IssueApi;
import org.sonarsource.sonarlint.core.serverapi.newcode.NewCodeApi;
import org.sonarsource.sonarlint.core.serverapi.organization.OrganizationApi;
import org.sonarsource.sonarlint.core.serverapi.plugins.PluginsApi;
import org.sonarsource.sonarlint.core.serverapi.projectbindings.ProjectBindingsApi;
import org.sonarsource.sonarlint.core.serverapi.push.PushApi;
import org.sonarsource.sonarlint.core.serverapi.qualityprofile.QualityProfileApi;
import org.sonarsource.sonarlint.core.serverapi.rules.RulesApi;
import org.sonarsource.sonarlint.core.serverapi.sca.ScaApi;
import org.sonarsource.sonarlint.core.serverapi.settings.SettingsApi;
import org.sonarsource.sonarlint.core.serverapi.source.SourceApi;
import org.sonarsource.sonarlint.core.serverapi.system.SystemApi;
import org.sonarsource.sonarlint.core.serverapi.users.UsersApi;

public class ServerApi {
  private final ServerApiHelper helper;

  public ServerApi(EndpointParams endpoint, HttpClient client) {
    this(new ServerApiHelper(endpoint, client));
  }

  public ServerApi(ServerApiHelper helper) {
    this.helper = helper;
  }

  public AuthenticationApi authentication() {
    return new AuthenticationApi(helper);
  }

  public ProjectBindingsApi projectBindings() {
    return new ProjectBindingsApi(helper);
  }

  public ComponentApi component() {
    return new ComponentApi(helper);
  }

  public DevelopersApi developers() {
    return new DevelopersApi(helper);
  }

  public HotspotApi hotspot() {
    return new HotspotApi(helper);
  }

  public OrganizationApi organization() {
    return new OrganizationApi(helper);
  }

  public IssueApi issue() {
    return new IssueApi(helper);
  }

  public SourceApi source() {
    return new SourceApi(helper);
  }

  public SettingsApi settings() {
    return new SettingsApi(helper);
  }

  public QualityProfileApi qualityProfile() {
    return new QualityProfileApi(helper);
  }

  public PluginsApi plugins() {
    return new PluginsApi(helper);
  }

  public RulesApi rules() {
    return new RulesApi(helper);
  }

  public SystemApi system() {
    return new SystemApi(helper);
  }

  public ProjectBranchesApi branches() {
    return new ProjectBranchesApi(helper);
  }

  public PushApi push() {
    return new PushApi(helper);
  }

  public NewCodeApi newCodeApi() {
    return new NewCodeApi(helper);
  }

  public FixSuggestionsApi fixSuggestions() {
    return new FixSuggestionsApi(helper);
  }

  public FeaturesApi features() {
    return new FeaturesApi(helper);
  }

  public ScaApi sca() {
    return new ScaApi(helper);
  }

  public UsersApi users() {
    return new UsersApi(helper);
  }

  public boolean isSonarCloud() {
    return helper.isSonarCloud();
  }

  public Optional<String> getOrganizationKey() {
    return helper.getOrganizationKey();
  }
}
