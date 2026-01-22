/*
ACR-230c83ca16b54fe99d05b5755aca1ba1
ACR-f45e3780d2cc4fb48d55588069ca8ac1
ACR-4a7c35f109764fc9b147737f335145d4
ACR-19c366c7b79745b5b1e5dd98c717660b
ACR-f6e58c71d9a2471baf4931731eaa4e40
ACR-1b89a622c5a54edc9398d0a3657c3111
ACR-aae312d3abd641e2b4a9ced4f6926b76
ACR-d39d4a48724f466d9c55842f33e5caaa
ACR-8fd994474a684ac3ab640c3d3b0ea003
ACR-164881b79ed245849aab08139f8509ad
ACR-e3a683dbaf40479b9a40e4da79e1c148
ACR-5175ce76bd9942d391f580e43f76fe55
ACR-975cca6405ea498cb9a21dda8509f28c
ACR-084229fc78374e1a89933a6a3d5b16e5
ACR-8887dbdf7d2c421f89baa1e0a070b4f6
ACR-f40d10f1738d4323a0ded4fdeb62141f
ACR-aa3e63d9969a4d83b0fd5d474463a579
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

/*ACR-254b101cdef6409192c53767800f0b33
ACR-ce92c598618a43608ff9270265e51253
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
