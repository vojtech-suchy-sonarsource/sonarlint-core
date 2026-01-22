/*
ACR-6d60ac3530324e1d892d1c018bdeaeec
ACR-d3af61922ac840c886d563eecc1a5651
ACR-3c5a67d92ba24e03a3314e7e0ca7d2b6
ACR-cfbbfa23e1594dbaa663fa5c0275d402
ACR-5fdc761c29a54c22b77f9577982d5ea8
ACR-54e842bb5c004a5988de691800947edb
ACR-ac40516253524240ad9b26d58f0aa018
ACR-ad53a97e3ee64daba82e5066ede1014d
ACR-542eed9c67fd458bafedfe0c4cab7460
ACR-b31d279dd78f4148b00086828051b74a
ACR-65a38db5c38b4de88a7b329ad2a60eb0
ACR-cb86652f92b74556912be8ec60d9de8c
ACR-7abd3500ddc647a898f1782740737218
ACR-1fdd55e08e5543b7ba214e3dffdf4058
ACR-b564c849e3ea4d029a1b1bc03514aea1
ACR-e90800929d044ba08d85a5633dde85c8
ACR-8db6f02b14bb49ccbdd832b976fe7b25
 */
package org.sonarsource.sonarlint.core.commons.storage;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;

/*ACR-b007528626084e498e175f9d5afe88c8
ACR-fab558bb6073407e912b8bd07527e49a
ACR-a283ef99319349b098e68b97af77d158
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
