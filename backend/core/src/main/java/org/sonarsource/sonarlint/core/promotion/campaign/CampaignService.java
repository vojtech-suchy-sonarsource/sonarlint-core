/*
ACR-234382af19674d46868990bd9af86b2a
ACR-b9fa8acac3964727bb610605897afdd9
ACR-8d35be0b01f54c5d9ef18aba905a4341
ACR-9987eeeb49794141b89c7d54ecc4e68b
ACR-416abe11a10b4791908abb8bc8bbc489
ACR-1abd639b8fed4a5c8565f4d9e5d01323
ACR-838ae060ed974809a46ce8f61688edfd
ACR-5579046502284c4cbf75f3a4642bf29f
ACR-045c01976fd14d628cf24114675a7a22
ACR-c15f104811b2423e9fa337e07445c241
ACR-2a0f09d1ca41439cae561a7ffc3ae1a1
ACR-2277027097754c0298bd119019bdede1
ACR-6154a42418ac45a796c1a3a2fd3f567b
ACR-48f9da8eb80d4c85afa1d75ce4cb7a21
ACR-5664c92a2b424e8eadd71d91ac3559ea
ACR-abf7b69a777e40f89cfb4ac4687e4af6
ACR-cefd54f973a24c3dbef9963b3ebb8654
 */
package org.sonarsource.sonarlint.core.promotion.campaign;

import com.google.common.util.concurrent.MoreExecutors;
import jakarta.annotation.PreDestroy;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.Period;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;
import org.sonarsource.sonarlint.core.commons.storage.local.FileStorageManager;
import org.sonarsource.sonarlint.core.commons.util.FailSafeExecutors;
import org.sonarsource.sonarlint.core.promotion.campaign.storage.CampaignsLocalStorage;
import org.sonarsource.sonarlint.core.rpc.protocol.SonarLintRpcClient;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.BackendCapability;
import org.sonarsource.sonarlint.core.rpc.protocol.backend.initialize.InitializeParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.OpenUrlInBrowserParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageActionItem;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.MessageType;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageRequestParams;
import org.sonarsource.sonarlint.core.rpc.protocol.client.message.ShowMessageRequestResponse;
import org.sonarsource.sonarlint.core.telemetry.InternalDebug;
import org.sonarsource.sonarlint.core.telemetry.TelemetryService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.sonarsource.sonarlint.core.promotion.campaign.FeedbackNotificationActionItem.LOVE_IT;
import static org.sonarsource.sonarlint.core.promotion.campaign.FeedbackNotificationActionItem.MAYBE_LATER;
import static org.sonarsource.sonarlint.core.promotion.campaign.FeedbackNotificationActionItem.SHARE_FEEDBACK;
import static org.sonarsource.sonarlint.core.promotion.campaign.storage.CampaignsLocalStorage.Campaign;

public class CampaignService {

  private static final SonarLintLogger LOG = SonarLintLogger.get();
  private static final Set<FeedbackNotificationActionItem> RESPONSES_TO_OPEN_URL = EnumSet.of(LOVE_IT, SHARE_FEEDBACK);
  private static final Map<String, Period> POSTPONE_PERIODS = Map.of(
    MAYBE_LATER.name(), Period.ofWeeks(1),
    "IGNORE", Period.ofMonths(6)
  );
  private static final String SIX_MINUTES_OF_SECONDS = "360";
  private static final int TWO_WEEKS = 14;

  private final String productKey;
  private final SonarLintRpcClient client;
  private final TelemetryService telemetryService;
  private final FileStorageManager<CampaignsLocalStorage> fileStorageManager;
  private final ScheduledExecutorService scheduledExecutor;
  private final ApplicationEventPublisher eventPublisher;
  private final boolean isEnabled;

  public CampaignService(@Qualifier("campaignsPath") Path campaignsPath, SonarLintRpcClient client, InitializeParams initializeParams, TelemetryService telemetryService,
    ApplicationEventPublisher eventPublisher) {
    this.productKey = initializeParams.getTelemetryConstantAttributes().getProductKey();
    this.client = client;
    this.telemetryService = telemetryService;
    this.fileStorageManager = new FileStorageManager<>(campaignsPath, CampaignsLocalStorage::new, CampaignsLocalStorage.class);
    this.eventPublisher = eventPublisher;
    this.scheduledExecutor = FailSafeExecutors.newSingleThreadScheduledExecutor("SonarLint Telemetry");
    this.isEnabled = initializeParams.getBackendCapabilities().contains(BackendCapability.PROMOTIONAL_CAMPAIGNS);
  }

  @PostConstruct
  public void checkCampaigns() {
    if (isEnabled && shouldShowFeedbackNotification()) {
      var initialDelayProperty = System.getProperty("sonarlint.internal.promotion.initialDelay", SIX_MINUTES_OF_SECONDS);
      var initialDelay = NumberUtils.toInt(initialDelayProperty, 360);
      scheduledExecutor.schedule(this::showFeedbackMessage, initialDelay, SECONDS);
    }
  }

  private boolean shouldShowFeedbackNotification() {
    var campaigns = fileStorageManager.getStorage().campaigns();
    var feedbackCampaign = campaigns.get(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN);
    if (feedbackCampaign != null) {
      var lastResponse = feedbackCampaign.lastUserResponse();
      return isPostponeResponse(lastResponse)
        && postponeTimePassed(lastResponse, feedbackCampaign);
    } else {
      return isInstalledLongEnough();
    }
  }

  private static boolean isPostponeResponse(String lastResponse) {
    return POSTPONE_PERIODS.containsKey(lastResponse);
  }

  private boolean isInstalledLongEnough() {
    return OffsetDateTime.now().minusDays(TWO_WEEKS).isAfter(telemetryService.installTime());
  }

  private static boolean postponeTimePassed(String lastResponse, Campaign feedbackCampaign) {
    var postpone = POSTPONE_PERIODS.get(lastResponse);
    var lastShown = feedbackCampaign.lastNotificationShownOn();
    return lastShown.plus(postpone).isBefore(LocalDate.now());
  }

  private void showFeedbackMessage() {
    fileStorageManager.tryUpdateAtomically(storage ->
      storage.campaigns()
        .put(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN,
          new Campaign(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN, LocalDate.now(), "IGNORE")));
    eventPublisher.publishEvent(new CampaignShownEvent(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN));
    var userChoice = client.showMessageRequest(new ShowMessageRequestParams(
      MessageType.INFO,
      "Enjoying SonarQube for IDE? We'd love to hear what you think.",
      getFeedbackNotificationActions()
    ));
    userChoice.thenAccept(this::handleFeedbackResponse);
  }

  private static List<MessageActionItem> getFeedbackNotificationActions() {
    return Stream.of(FeedbackNotificationActionItem.values())
      .map(FeedbackNotificationActionItem::toMessageActionItem)
      .toList();
  }

  private void handleFeedbackResponse(ShowMessageRequestResponse response) {
    Optional.of(response)
      .map(ShowMessageRequestResponse::getSelectedKey)
      .ifPresent(this::handleFeedbackResponse);
  }

  private void handleFeedbackResponse(String responseOption) {
    fileStorageManager.tryUpdateAtomically(storage -> storage.campaigns().put(
      CampaignConstants.FEEDBACK_2026_01_CAMPAIGN,
      new Campaign(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN, LocalDate.now(), responseOption)));
    eventPublisher.publishEvent(new CampaignResolvedEvent(CampaignConstants.FEEDBACK_2026_01_CAMPAIGN,
      responseOption));

    var response = EnumUtils.getEnum(FeedbackNotificationActionItem.class, responseOption);
    if (RESPONSES_TO_OPEN_URL.contains(response)) {
      var url = CampaignConstants.urlToOpen(response, productKey);
      if (url != null) {
        client.openUrlInBrowser(new OpenUrlInBrowserParams(url));
      } else {
        redirectToCommunityIfNoLinkFound();
      }
    }
  }

  private void redirectToCommunityIfNoLinkFound() {
    var showMessageRequestParams = new ShowMessageRequestParams(MessageType.INFO,
      "Could not find feedback link for " + productKey + ". Please consider sharing your feedback directly on our community forum",
      List.of(new MessageActionItem("OPEN_COMMUNITY", "Open Community Forum", true)));

    client.showMessageRequest(showMessageRequestParams)
      .thenAccept(response -> {
        if (response.getSelectedKey() != null && response.getSelectedKey().equals("OPEN_COMMUNITY")) {
          client.openUrlInBrowser(new OpenUrlInBrowserParams("https://community.sonarsource.com/c/sl/11"));
        }
      });
  }

  @PreDestroy
  public void close() {
    if ((!MoreExecutors.shutdownAndAwaitTermination(scheduledExecutor, 1, TimeUnit.SECONDS)) && (InternalDebug.isEnabled())) {
      LOG.error("Failed to stop Campaign Service executor");
    }
  }
}
