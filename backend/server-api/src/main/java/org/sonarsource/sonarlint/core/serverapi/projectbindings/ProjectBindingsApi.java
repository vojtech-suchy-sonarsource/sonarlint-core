/*
ACR-175408a27627484bb4aeb8d5cde08b8f
ACR-9e731029e7654dd3b4dd085262858353
ACR-9b248fd7b9fa47498462db151723759a
ACR-21abfdeed2b344cd80239ce54bc3fdc0
ACR-2c118922f6eb48099f5142b245d04f25
ACR-8b06d71a68b0457b8734d96cae936679
ACR-d1139f212a964e9493df6a3b784f163a
ACR-319007e5c8d5451d8f75a7dec8ea9f97
ACR-a526fca0c74441d1b68c2df4c693c7ab
ACR-30dea2c585b94f5299f45fd12854afa5
ACR-0245b8aaed6848d6a43120d10325d1ee
ACR-19cd25eb5fc3417eb4252d8e71ba82f8
ACR-23bb2fafdb6d47c9b6056a42b96ce606
ACR-4186fda07b6c4627867d6105440c4555
ACR-d1a5c91123544b87a76f31166e9f3576
ACR-e099d8eb208146b9b504e20ae7c40f9d
ACR-98d748d656d948f2a571bea0c2b1bb49
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
