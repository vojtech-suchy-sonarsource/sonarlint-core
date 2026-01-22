/*
ACR-d65bef48f7574761957b5a3cd34bae9e
ACR-2483bea2e9e1453288d2be93638c6f3a
ACR-4a97c8739cd3458297f65cb2a24c97ea
ACR-3a3146c32da0414ca41bfd11cd24431b
ACR-cccb34dadd634caf81fc3c567b450ae5
ACR-aa4e7348f6224445afb16318d2982c7f
ACR-e0c7141d3cbf4fc49c35da68f5394c91
ACR-9dcd500255e64c1ab78a2c113ec28769
ACR-b45284fb541e4fd0a8885c18c40cd32b
ACR-d1f1ba224f4b4c938baa25de3219ccb8
ACR-cb14c8a1c5044ba59246ecf17518637f
ACR-eed14578a07a41b98144158cc872db77
ACR-a82706849fcd42b3893e3adbfbcf25de
ACR-8a65f64bda9b4c37b8757602d4978650
ACR-3412af9b71034889877d7417c666639f
ACR-2e4bf7828a254072867c4817fc4cb257
ACR-1c872341eeca49fb9d0762b1c8e413df
 */
package org.sonarsource.sonarlint.core.serverconnection.aicodefix;

import java.util.Optional;
import java.util.Set;
import org.jooq.DSLContext;
import org.sonarsource.sonarlint.core.commons.log.SonarLintLogger;

import static org.sonarsource.sonarlint.core.commons.storage.model.Tables.AI_CODEFIX_SETTINGS;

/*ACR-50c1588cadbe4566a7831487aa481167
ACR-1d170d6f2c634dfdb56ef01893cb365c
ACR-2a267e2c4ab04869a29037ca95e71c23
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
