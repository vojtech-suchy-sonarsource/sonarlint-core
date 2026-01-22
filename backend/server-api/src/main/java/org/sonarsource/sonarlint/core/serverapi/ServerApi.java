/*
ACR-9f0364c3b27a4a09bcb355e91afaaee5
ACR-76d1ad8d63be4d1098e6529cf85b7242
ACR-4a1b08d47c8a4fb0ac9032730937a4ee
ACR-4600aa1402b14260adc2d394a6fadaf1
ACR-6f7887ad34754e879a7ec3205678371d
ACR-b0101adeb9ea41ef86cf64bd639f0626
ACR-bfab5f1188294fa5805b5ae99780d186
ACR-30d59d6511c54faf88ef26cbaecc0b71
ACR-d4ce2613e19447c58f864f93ad4cd715
ACR-6095deeff01148bca69ecc3284165d87
ACR-49026a9a9ad446e6ae3c32f878374548
ACR-ea791b316fec496291bcfe05d10edf44
ACR-c55f430bb44843c49094e82268050351
ACR-cc451ae9bacb4fb5a4125cf464b75a53
ACR-80991fa5395b46b48963733bf2369853
ACR-6ce30580a59f46a188375f6b68f4bc91
ACR-2b953d0720cc4bddbc3a61e7fb923849
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
