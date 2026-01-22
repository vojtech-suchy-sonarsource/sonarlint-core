/*
ACR-0d234d6767974796995aa412d868a4fc
ACR-2cbf6f87e5df403d8b067162287052eb
ACR-9fbcbee0b09447cdaaa3c7fcd90731f5
ACR-5736bcfe61e34a69bbc2e0e01747c05c
ACR-8d4e3ecbca6e48ada73c09cfadc68ef6
ACR-3fe9a15cc9f0429082259b1b1c28eb4f
ACR-8e943cf8236f4430ae095cc5e45cce4a
ACR-eac42949f12d463a89f6ab23158f03d9
ACR-9183c15d60dd48c697dd021c882138f0
ACR-292e088e84c5459bb782c533c0b7d099
ACR-6876a29606024b26b56f595e0ba43544
ACR-13db4dc6015246c8bbf29b0bd33b29bd
ACR-d29414e34fc9430bb18f2294e3a9ade9
ACR-3c6559ca3e9c4db5a2201a0a5dee8f67
ACR-09358df22f4046839b14a3ffd77b94d8
ACR-43aabb4af0834e07bd544618cd44738b
ACR-a08cc62ff69047ba8710b17a1bfa4c66
 */
package org.sonarsource.sonarlint.core.serverapi.projectbindings;

import com.google.gson.Gson;
import javax.annotation.CheckForNull;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.progress.SonarLintCancelMonitor;
import org.sonarsource.sonarlint.core.serverapi.ServerApiHelper;
import org.sonarsource.sonarlint.core.serverapi.UrlUtils;

public class ProjectBindingsApi {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final ServerApiHelper serverApiHelper;

  public ProjectBindingsApi(ServerApiHelper serverApiHelper) {
    this.serverApiHelper = serverApiHelper;
  }

  @CheckForNull
  public SQCProjectBindingsResponse getSQCProjectBindings(String url, SonarLintCancelMonitor cancelMonitor) {
    var encodedUrl = UrlUtils.urlEncode(url);

    var path = "/dop-translation/project-bindings?url=" + encodedUrl;

    try (var response = serverApiHelper.apiGet(path, cancelMonitor)) {
      if (response.isSuccessful()) {
        var responseBody = response.bodyAsString();
        var dto = new Gson().fromJson(responseBody, SQCProjectBindingsResponseDto.class);
        var bindings = dto.bindings();

        if (!bindings.isEmpty()) {
          return new SQCProjectBindingsResponse(bindings.get(0).projectId());
        }
      } else {
        LOG.warn("Failed to retrieve project bindings for URL: {} (status: {})", url, response.code());
      }
    } catch (Exception e) {
      LOG.error("Error retrieving project bindings for URL: {}", url, e);
    }

    return null;
  }

  @CheckForNull
  public SQSProjectBindingsResponse getSQSProjectBindings(String url, SonarLintCancelMonitor cancelMonitor) {
    var encodedUrl = UrlUtils.urlEncode(url);

    var path = "/api/v2/dop-translation/project-bindings?repositoryUrl=" + encodedUrl;

    try (var response = serverApiHelper.get(path, cancelMonitor)) {
      if (response.isSuccessful()) {
        var responseBody = response.bodyAsString();
        var dto = new Gson().fromJson(responseBody, SQSProjectBindingsResponseDto.class);
        var bindings = dto.projectBindings();

        if (!bindings.isEmpty()) {
          return new SQSProjectBindingsResponse(dto.projectBindings().get(0).projectId(), dto.projectBindings().get(0).projectKey());
        }
      } else {
        LOG.warn("Failed to retrieve project bindings for URL: {} (status: {})", url, response.code());
      }
    } catch (Exception e) {
      LOG.error("Error retrieving project bindings for URL: {}", url, e);
    }

    return null;
  }
}
