/*
ACR-4c13edd2783b4fde9c64aa0f337b383a
ACR-246ad0e5efcd44ae844d9836713fdaf7
ACR-25a04832966749279fa317f1f09075b4
ACR-764675328cbb41df9eefec7351337610
ACR-a47da19f55d849c5b003dbea3c38fbfc
ACR-0c7b5899923f43e08b181113f6c49708
ACR-6b809ac53e074c9f83ffb0cda8b082e6
ACR-2bcd108ce60d4824b74973982455617e
ACR-d6c6744299904c93929703be393d08b4
ACR-d0b433bfc86b40a29a0be2df34dad395
ACR-959991d3302b43e4b0af3732984de96b
ACR-87f2751bde404201b136e7c9841ea8d9
ACR-d0a398203b0c45c7a1848f260678ef78
ACR-3079b3fc5780406faeb74f083018475e
ACR-c2dd6878a19940fdae91bbc856c31fc6
ACR-2aca487d88a449899c6e4362bc66bd7b
ACR-d75c390bfbb1484d98bd2a90cc5e9ed3
 */
package org.sonarsource.sonarlint.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.common.TransientSonarCloudConnectionDto;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.connection.org.OrganizationDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.Either;
import org.sonarsource.sonarlint.core.rpc.protocol.common.TokenDto;
import org.sonarsource.sonarlint.core.rpc.protocol.common.UsernamePasswordDto;

import static java.util.Objects.requireNonNull;
import static org.sonarsource.sonarlint.core.commons.log.SonarLintLogger.singlePlural;

/*ACR-72a9dacb52654d24b20dc41b7604166b
ACR-329491e74a5344529134a631d6780c46
 */
public class OrganizationsCache {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private final SonarQubeClientManager sonarQubeClientManager;

  private final Cache<Either<TokenDto, UsernamePasswordDto>, TextSearchIndex<OrganizationDto>> textSearchIndexCacheByCredentials = CacheBuilder.newBuilder()
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .build();

  public OrganizationsCache(SonarQubeClientManager sonarQubeClientManager) {
    this.sonarQubeClientManager = sonarQubeClientManager;
  }

  public List<OrganizationDto> fuzzySearchOrganizations(TransientSonarCloudConnectionDto transientSonarCloudConnection, String searchText, SonarLintCancelMonitor cancelMonitor) {
    return getTextSearchIndex(transientSonarCloudConnection, cancelMonitor).search(searchText)
      .entrySet()
      .stream()
      .sorted(Comparator.comparing(Map.Entry<OrganizationDto, Double>::getValue).reversed()
        .thenComparing(e -> e.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
      .limit(10)
      .map(Map.Entry::getKey)
      .toList();
  }

  public TextSearchIndex<OrganizationDto> getTextSearchIndex(TransientSonarCloudConnectionDto transientSonarCloudConnection, SonarLintCancelMonitor cancelMonitor) {
    try {
      return textSearchIndexCacheByCredentials.get(transientSonarCloudConnection.getCredentials(), () -> {
        LOG.debug("Load user organizations...");
        List<OrganizationDto> orgs;
        try {
          orgs = sonarQubeClientManager.getForTransientConnection(Either.forRight(transientSonarCloudConnection))
            .organization()
            .listUserOrganizations(cancelMonitor).stream().map(o -> new OrganizationDto(o.getKey(), o.getName(), o.getDescription())).toList();
        } catch (Exception e) {
          LOG.error("Error while querying SonarCloud organizations", e);
          return new TextSearchIndex<>();
        }
        if (orgs.isEmpty()) {
          LOG.debug("No organizations found");
          return new TextSearchIndex<>();
        } else {
          LOG.debug("Creating index for {} {}", orgs.size(), singlePlural(orgs.size(), "organization"));
          var index = new TextSearchIndex<OrganizationDto>();
          orgs.forEach(org -> index.index(org, org.getKey() + " " + org.getName()));
          return index;
        }
      });
    } catch (ExecutionException e) {
      throw new IllegalStateException(e.getCause());
    }
  }

  public List<OrganizationDto> listUserOrganizations(TransientSonarCloudConnectionDto transientSonarCloudConnection, SonarLintCancelMonitor cancelMonitor) {
    textSearchIndexCacheByCredentials.invalidate(transientSonarCloudConnection.getCredentials());
    return getTextSearchIndex(transientSonarCloudConnection, cancelMonitor).getAll();
  }

  @CheckForNull
  public OrganizationDto getOrganization(TransientSonarCloudConnectionDto transientSonarCloudConnection, SonarLintCancelMonitor cancelMonitor) {
    return sonarQubeClientManager.getForTransientConnection(Either.forRight(transientSonarCloudConnection))
      .organization().searchOrganization(requireNonNull(transientSonarCloudConnection.getOrganization()), cancelMonitor)
      .map(o -> new OrganizationDto(o.getKey(), o.getName(), o.getDescription())).orElse(null);
  }

}
