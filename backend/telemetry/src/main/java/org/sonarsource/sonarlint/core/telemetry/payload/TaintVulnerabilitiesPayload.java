/*
ACR-488aa4e96e304b85aa2b1537432ce0b2
ACR-fc1b311286db4515a64b49dbc42830ea
ACR-4e304e18a4a743ba98d50d4cdd399dd1
ACR-39fa2ba98b3742fc94cd67a76aff2ca2
ACR-eaec79f378304e96858e3520d375402f
ACR-2dc8732b7fcb4ffeb815ebf1f38a43e0
ACR-d15d94f9c8a54d559f6c7e483a3da82a
ACR-127fe5fc21e24c6f930f6d8a3d45a299
ACR-cf5b59bc8b2a484794e04be0f9a95cb9
ACR-4ec661d904c546c189542afc0d99b31e
ACR-22c4019e9aff4b35b96f4feb1f945ae6
ACR-8f203f51093944209bfb749424fcbaa6
ACR-798f43152d9a4711a603124c21aed015
ACR-ea9d6c0dbbe64d03b177e35effdaf4d7
ACR-206271272c3f492f9b4ee63ec73e9db4
ACR-8f3e5b608e5242779976f42dbd7cfa3c
ACR-3109d00ddb994f26accd1be90bc2974a
 */
package org.sonarsource.sonarlint.core.telemetry.payload;

import com.google.gson.annotations.SerializedName;

public record TaintVulnerabilitiesPayload(@SerializedName("investigated_locally_count") int investigatedLocallyCount,
                                          @SerializedName("investigated_remotely_count") int investigatedRemotelyCount) {
}
