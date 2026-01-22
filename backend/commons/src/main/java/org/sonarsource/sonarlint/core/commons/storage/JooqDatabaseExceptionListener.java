/*
ACR-ea137b3b811349b599ba2bcdcb6fad48
ACR-4d3fc9290be1455f9cb491828f906f91
ACR-7e64f1b80ef946d3a012027d80d708db
ACR-72ed15291f4c4af9b2aaefbf430e6065
ACR-ce98482002564e1699f8fca068d1b3ca
ACR-a7dbda2563dd4bada0918ae6701e5668
ACR-da75d667bf4e45728f830d67df369ff1
ACR-4c98ccfd17a445ea8c5422b6c8a418b5
ACR-19cec7d1ba8948c4a846238257086d64
ACR-d868b5d3b93444c3950cea462add10c0
ACR-f71bc6b406cd41c0bf58a5dc19860b14
ACR-fcd30ab333c54dab96a77c30f8369b45
ACR-91a2f59053534832b6749881ab6b5616
ACR-8aadb2227f5047b2ad748ec461100c0a
ACR-ada5c79b69984fc19a5a22993c53fcc8
ACR-e07ca32942604a8f92074bb325bcc4b6
ACR-513436f899714d51ab830c324c3e7617
 */
package org.sonarsource.sonarlint.core.commons.storage;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

/*ACR-66eb8cd6d2d646c39c0e6d34775c50ca
ACR-eaaa9dc020914181b55c84a8fd644109
ACR-f5b64a4e307147c59920a759aa6dcbf2
 */
public class JooqDatabaseExceptionListener implements ExecuteListener {

  @Override
  public void exception(ExecuteContext ctx) {
    var exception = ctx.exception();
    if (exception == null) {
      return;
    }

    var sqlException = ctx.sqlException();
    var exceptionToReport = sqlException != null ? sqlException : exception;
    var sql = ctx.sql();

    DatabaseExceptionReporter.capture(exceptionToReport, "runtime", "jooq.execute", sql);
  }
}
