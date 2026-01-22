/*
ACR-a945137dc5bf43a3816d37486b669cb7
ACR-54081d47d444404c9664a3cd10db4088
ACR-732cffb03f724837abdfff543738dac7
ACR-35ba2bb132ef408f9c0c9513dac9eedb
ACR-3cb185e5b6a9425fb75680865da9fc18
ACR-e8a833e29ceb43b49ba630c73cce7c18
ACR-617f3ee4061240b0947a007620972447
ACR-88f20c35023d45c585bb16c9b2120923
ACR-d72dc76f579b4effad3eaffff7b64d4f
ACR-8d351db0a04047c0afe72b74d57e875f
ACR-7da89770d9a2422b9883225a152ea7ae
ACR-4933089e27f34a51a022a391da6bb165
ACR-928eb8c064d84fac94c79f739796dc53
ACR-247c81fe3852427c904a948f4ac0746c
ACR-22398d8ec8a349fdaaf047d09991ff8c
ACR-aa2f32550ace4f3f8e7ec1ff6e365f52
ACR-247c62ae8f074d679ab5ec23e6ddf1cf
 */
package org.sonarsource.sonarlint.core.analysis.container.analysis.filesystem;

import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.InputFile;

/*ACR-05389160fdad4b71a05500d5121ecea7
ACR-1b65b02380794722a714b9057510e2a3
 */
class NotPredicate extends AbstractFilePredicate {

  private final FilePredicate predicate;

  NotPredicate(FilePredicate predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean apply(InputFile f) {
    return !predicate.apply(f);
  }

}
