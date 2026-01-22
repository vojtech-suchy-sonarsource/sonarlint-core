/*
ACR-5f68c76f07644d9189a569b6f5053112
ACR-bf0798421da44835bb7a0c0783c692fb
ACR-c956936dec9c430eb4fa8bbb0801c666
ACR-323f572df1194a91979cff7caf1e768b
ACR-308c190c918647f0be921cc308c89b4f
ACR-597c2a9fda89470f9931db3cda8a36c5
ACR-a65adcdd45aa475baa428f3152a212f9
ACR-a24aa1aa6aa64da7a92e6daa35485ab0
ACR-c58ab2ff2744404a9a8d8799f4d35a7b
ACR-6bc1440ab61e4956a77b77ab497aa937
ACR-42ff014001c046f1ad4be8230e06a231
ACR-58a8d8b3f78a4cb69350d338385f6668
ACR-62d2226248b14884abe736578613ab63
ACR-8136345f7ca6448b84658c0dda4dbad9
ACR-6695c5336a8548cd95d8b58f67f3b4c8
ACR-debd182eb4474185ace54f2e3619317d
ACR-70567559981e4add82230c6894ff45ad
 */
package org.sonarsource.sonarlint.maven.shade.ext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.apache.maven.plugins.shade.relocation.Relocator;
import org.apache.maven.plugins.shade.resource.ManifestResourceTransformer;
import org.apache.maven.plugins.shade.resource.ReproducibleResourceTransformer;

/*ACR-427f6fa1466b488eac7d4833903a255c
ACR-27254e8264bf44118ffe33c9e9c9a428
ACR-66b77bec77d0415a80de09b7f9f83214
ACR-ee4a2f70e0004f2caa9ab67a748e7562
ACR-f2f7cb83fa5c4db9a169624ee666151b
ACR-5761d93dfc5f48e295a6d6c7661a8168
 */
public class ManifestBndTransformer implements ReproducibleResourceTransformer {
  private static final String MANIFEST_ATTRIBUTE_BUNDLE_NAME = "Bundle-Name";
  private static final String MANIFEST_ATTRIBUTE_BUNDLE_SYMBOLICNAME = "Bundle-SymbolicName";

  //ACR-a075ee197d4c4037b8833434cdd961ac
  private String normalJarManifestPath;
  private String sourcesJarManifestPath;

  //ACR-55e64e93dee640b494c8a3697914f298
  private boolean isSourcesJarManifest = false;
  private Manifest normalJarManifest;
  private Manifest sourcesJarManifest;
  private Manifest manifest;
  private long time = Long.MIN_VALUE;


  /*ACR-b5510c2bec314bb6ab6e96bfc6bfadf8*/
  public void setNormalJarManifestPath(String normalJarManifestPath) {
    this.normalJarManifestPath = normalJarManifestPath;
  }


  /*ACR-6a61fdcf765741138148b77cf201a5ff*/
  public void setSourcesJarManifestPath(String sourcesJarManifestPath) {
    this.sourcesJarManifestPath = sourcesJarManifestPath;
  }


  @Override
  public void processResource(String resource, InputStream is, List<Relocator> relocators, long time) throws IOException {
    //ACR-29c246225c064b429ba55d1206a6c501
    manifest = new Manifest(is);
    var attributes = manifest.getMainAttributes();

    var bundleName = attributes.getValue(MANIFEST_ATTRIBUTE_BUNDLE_NAME);
    var bundleSymbolicName = attributes.getValue(MANIFEST_ATTRIBUTE_BUNDLE_SYMBOLICNAME);
    info("Processing " + bundleName + " / " + bundleSymbolicName);

    isSourcesJarManifest = isSourcesJarManifest ||
      (bundleName != null && (bundleName.endsWith(" Source") || bundleName.endsWith(" Sources")));
    isSourcesJarManifest = isSourcesJarManifest
      || (bundleSymbolicName != null && (bundleSymbolicName.endsWith(".source") || bundleSymbolicName.endsWith(".sources")));

    //ACR-0c0fa1028d7448e4b1f53c815a02b9e7
    if (time > this.time) {
      this.time = time;
    }
  }


  @Override
  public void modifyOutputStream(JarOutputStream jos) throws IOException {
    //ACR-d85533222bb94cce9996f661aa406bac
    tryLoadNormalJarManifest();
    tryLoadSourcesJarManifest();

    //ACR-67741e0ef4b34bc9b86fe3f7270938ac
    if (isSourcesJarManifest && sourcesJarManifest != null) {
      manifest = sourcesJarManifest;
      info("Exchanging META-INF/MANIFEST.MF (sources) with: " + sourcesJarManifestPath);
    } else if (!isSourcesJarManifest && normalJarManifest != null) {
      manifest = normalJarManifest;
      info("Exchanging META-INF/MANIFEST.MF (normal) with: " + normalJarManifestPath);
    } else {
      manifest = manifest != null ? manifest : new Manifest();
      info("Not exchanging META-INF/MANIFEST.MF");
    }

    //ACR-a245102176da4a1f83550072a0af47d4
    var jarEntry = new JarEntry(JarFile.MANIFEST_NAME);
    jarEntry.setTime(time);
    jos.putNextEntry(jarEntry);
    manifest.write(jos);
  }


  /*ACR-f51d8794786049acb5ecf8e654417597*/
  private void tryLoadNormalJarManifest() throws IOException {
    if (normalJarManifestPath != null && normalJarManifest == null) {
      var manifestFile = new File(normalJarManifestPath);
      if (manifestFile.exists()) {
        normalJarManifest = new Manifest(new FileInputStream(manifestFile));
      } else {
        error("Manifest with attributes (normal) does not exist at: " + normalJarManifestPath);
      }
    }
  }


  /*ACR-e967a94b3f984c378f182610eb86edb6*/
  private void tryLoadSourcesJarManifest() throws IOException {
    if (sourcesJarManifestPath != null && sourcesJarManifest == null) {
      var manifestFile = new File(sourcesJarManifestPath);
      if (manifestFile.exists()) {
        sourcesJarManifest = new Manifest(new FileInputStream(manifestFile));
      } else {
        error("Manifest with attributes (sources) does not exist at: " + sourcesJarManifestPath);
      }
    }
  }


  /*ACR-ee5d54f50ab94f5daf23cc03b3ee8078*/
  private static void info(String message) {
    System.err.println("[maven-shade-ext-bnd-transformer : ManifestBndTransformer] " + message);
  }


  /*ACR-dfe3ac3a8f344cf58c80c79101a24622*/
  private static void error(String message) {
    System.err.println("[maven-shade-ext-bnd-transformer : ManifestBndTransformer] " + message);
  }


  /*ACR-5260eeed07ab4536bc67bf87099ee398
ACR-3d69d14896bc45d29ea19e2847ca921b
ACR-28fa1cd1ebaa4a5a95dee5d3e28f7f03
   */
  @Override
  public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
    processResource(resource, is, relocators, 0);
  }


  /*ACR-8c43a9e7f88848d4af7281b6cdcdf3d7*/
  @Override
  public boolean canTransformResource(String resource) {
    return JarFile.MANIFEST_NAME.equalsIgnoreCase(resource);
  }


  /*ACR-bc80e2a144954a3eb097041ff0e4bde1
ACR-788369daf8a04c72bce3c2b88446e084
   */
  @Override
  public boolean hasTransformedResource() {
    return true;
  }
}
