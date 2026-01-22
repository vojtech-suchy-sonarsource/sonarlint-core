/*
ACR-88af836483b34e3abee86666e11fdb1d
ACR-922667e8356343e3ae8a759014dc49da
ACR-eac05302dd3042d1a73fd39f684ded84
ACR-c826fd9a517c4dc48b7ca89df320e9e9
ACR-f65b6d5ddf434a5f9a38927b7743bfed
ACR-9d7291fb952d4425b0aa83a254eb0318
ACR-5f3bfc7e43084ab791961a3cee18d91d
ACR-fdc5e4187aa34d2486bcc55f8c61aac7
ACR-4416c19188724c228d89156f81246d7d
ACR-3ca79b77b00d4b88bc36eb41746e3a59
ACR-a875200a949740cea4922052287ad7b6
ACR-75c1d923a0cb47f082e8af1a9ad47160
ACR-2fe9cb38af9f4cdfab2f61f6a5622903
ACR-9052345c9b9e44d7896f85a8f136567d
ACR-c8c35218386049cdab11cf7d714b3cf7
ACR-e474b6a87b3449d589dba52af44cacf0
ACR-15f22cb10d394906a27ce8bf12e8f541
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Optional;
import java.util.Set;
import org.jooq.DSLContext;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.AI_CODEFIX_SETTINGS;

/*ACR-e2c9b29c51c944e9874eae800b9b13b1
ACR-729f3ea1ba1b4dfe871e064a713a6976
ACR-f6c1600668eb428f9904679f2949bacc
 */
public class AiCodeFixRepository {
  private static final SonarLintLogger LOG = SonarLintLogger.get();

  private final DSLContext database;

  public AiCodeFixRepository(DSLContext dslContext) {
    this.database = dslContext;
  }

  public Optional<AiCodeFix> get(String connectionId) {
    var rec = database
      .select(AI_CODEFIX_SETTINGS.SUPPORTED_RULES, AI_CODEFIX_SETTINGS.ORGANIZATION_ELIGIBLE, AI_CODEFIX_SETTINGS.ENABLEMENT, AI_CODEFIX_SETTINGS.ENABLED_PROJECT_KEYS)
      .from(AI_CODEFIX_SETTINGS)
      .where(AI_CODEFIX_SETTINGS.CONNECTION_ID.eq(connectionId))
      .fetchOne();
    if (rec == null) {
      return Optional.empty();
    }
    var supportedRules = rec.get(AI_CODEFIX_SETTINGS.SUPPORTED_RULES);
    var organizationEligible = Boolean.TRUE.equals(rec.get(AI_CODEFIX_SETTINGS.ORGANIZATION_ELIGIBLE));
    var enablement = AiCodeFix.Enablement.valueOf(rec.get(AI_CODEFIX_SETTINGS.ENABLEMENT));
    var enabledProjectKeys = rec.get(AI_CODEFIX_SETTINGS.ENABLED_PROJECT_KEYS);
    return Optional.of(new AiCodeFix(connectionId, supportedRules, organizationEligible, enablement, enabledProjectKeys));
  }

  public void upsert(AiCodeFix entity) {
    database
      .insertInto(AI_CODEFIX_SETTINGS, AI_CODEFIX_SETTINGS.CONNECTION_ID, AI_CODEFIX_SETTINGS.SUPPORTED_RULES, AI_CODEFIX_SETTINGS.ORGANIZATION_ELIGIBLE,
        AI_CODEFIX_SETTINGS.ENABLEMENT, AI_CODEFIX_SETTINGS.ENABLED_PROJECT_KEYS)
      .values(entity.connectionId(), entity.supportedRules(), entity.organizationEligible(), entity.enablement().name(), entity.enabledProjectKeys())
      .onDuplicateKeyUpdate()
      .set(AI_CODEFIX_SETTINGS.SUPPORTED_RULES, entity.supportedRules())
      .set(AI_CODEFIX_SETTINGS.ORGANIZATION_ELIGIBLE, entity.organizationEligible())
      .set(AI_CODEFIX_SETTINGS.ENABLEMENT, entity.enablement().name())
      .set(AI_CODEFIX_SETTINGS.ENABLED_PROJECT_KEYS, entity.enabledProjectKeys())
      .execute();
  }

  public void deleteUnknownConnections(Set<String> knownConnectionIds) {
    database.dsl().deleteFrom(AI_CODEFIX_SETTINGS)
      .where(AI_CODEFIX_SETTINGS.CONNECTION_ID.notIn(knownConnectionIds))
      .execute();
  }
}
