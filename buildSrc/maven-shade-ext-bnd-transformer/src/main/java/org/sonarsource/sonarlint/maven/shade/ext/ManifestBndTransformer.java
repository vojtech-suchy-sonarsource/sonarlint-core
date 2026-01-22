/*
ACR-074fcdb0e3694f128fd19b8027898cc2
ACR-509a80b9c52244fe9bdef8ec54db90a2
ACR-14ebd6bb740d4f79b5b93e2922740ec7
ACR-a8a49c47e43e4c048e8754cbeb5bdfd1
ACR-2461f6118208460a95bd40b74b8c7136
ACR-b7b174a2fe1d4842b47216ac071310bd
ACR-730923534a80406984fa9ab5dd678124
ACR-05e5b5b18a6646028c9a34a2a1462232
ACR-1fba46d83903477289d9fe149f517c09
ACR-7f1801bceec8478b95d27ce13b5b681f
ACR-919e5eaaab2f4512b113074b97718cff
ACR-c46f51dd891148838cbd37a5eb6aa069
ACR-13c0065a5c7e46f2893f5746b3a433a1
ACR-8051aab7a66545859ac49022002b4ceb
ACR-b4591bee96374535b7ac03d5bb0a0d16
ACR-d13058817d1f41fdbdf93c3786119d4e
ACR-4e54cdb8e9984782bba813624f79c54b
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

/*ACR-365c267b266b41989f24dbc7b2ca9f14
ACR-70cd98c1b9764a03b783b1c03125a1c6
ACR-e5f27c1ccfd3443aa1f4cdda3b315f31
ACR-0c46238937d24d97a465f963fca3e845
ACR-7c373690df8e4308a1035cbcd5e2fe20
ACR-569c4e72f46e4abbb6372079a5f28518
 */
public class ManifestBndTransformer implements ReproducibleResourceTransformer {
  private static final String MANIFEST_ATTRIBUTE_BUNDLE_NAME = "Bundle-Name";
  private static final String MANIFEST_ATTRIBUTE_BUNDLE_SYMBOLICNAME = "Bundle-SymbolicName";

  //ACR-60bdbbce00124da889ad33ea0dbfcb1e
  private String normalJarManifestPath;
  private String sourcesJarManifestPath;

  //ACR-fd4f7b9b0ffc4928b18b5d2cbc60a97c
  private boolean isSourcesJarManifest = false;
  private Manifest normalJarManifest;
  private Manifest sourcesJarManifest;
  private Manifest manifest;
  private long time = Long.MIN_VALUE;


  /*ACR-f935d64a8fb54138865c503a0b65f71f*/
  public void setNormalJarManifestPath(String normalJarManifestPath) {
    this.normalJarManifestPath = normalJarManifestPath;
  }


  /*ACR-85d66878a6554ccaa29f4459f8b11a99*/
  public void setSourcesJarManifestPath(String sourcesJarManifestPath) {
    this.sourcesJarManifestPath = sourcesJarManifestPath;
  }


  @Override
  public void processResource(String resource, InputStream is, List<Relocator> relocators, long time) throws IOException {
    //ACR-992649fc34e44f089171a0bd88348e5b
    manifest = new Manifest(is);
    var attributes = manifest.getMainAttributes();

    var bundleName = attributes.getValue(MANIFEST_ATTRIBUTE_BUNDLE_NAME);
    var bundleSymbolicName = attributes.getValue(MANIFEST_ATTRIBUTE_BUNDLE_SYMBOLICNAME);
    info("Processing " + bundleName + " / " + bundleSymbolicName);

    isSourcesJarManifest = isSourcesJarManifest ||
      (bundleName != null && (bundleName.endsWith(" Source") || bundleName.endsWith(" Sources")));
    isSourcesJarManifest = isSourcesJarManifest
      || (bundleSymbolicName != null && (bundleSymbolicName.endsWith(".source") || bundleSymbolicName.endsWith(".sources")));

    //ACR-d4a923a51ed34abd845301e7e6f67c88
    if (time > this.time) {
      this.time = time;
    }
  }


  @Override
  public void modifyOutputStream(JarOutputStream jos) throws IOException {
    //ACR-cb8447b233a54cf5b9fd142555a1b41e
    tryLoadNormalJarManifest();
    tryLoadSourcesJarManifest();

    //ACR-112d111b713a4c9fae645bb1cbda2280
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

    //ACR-ca93fedf1b6d427088e2f958a6d3d9de
    var jarEntry = new JarEntry(JarFile.MANIFEST_NAME);
    jarEntry.setTime(time);
    jos.putNextEntry(jarEntry);
    manifest.write(jos);
  }


  /*ACR-17c6c758c96f4e3882476b97ba693152*/
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


  /*ACR-5fbd1ffbbc694bf6bf67cb673711ca9c*/
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


  /*ACR-c4202773d0d74c9eacfd1e4ce1f6b79e*/
  private static void info(String message) {
    System.err.println("[maven-shade-ext-bnd-transformer : ManifestBndTransformer] " + message);
  }


  /*ACR-d4830f06aaaa4c568bec09498b615606*/
  private static void error(String message) {
    System.err.println("[maven-shade-ext-bnd-transformer : ManifestBndTransformer] " + message);
  }


  /*ACR-abee233e49a1443e9a3f3ab3909d1cca
ACR-b2dffcb4c37940138ec605c0fb3958f5
ACR-25636e1249364e64b44c44d949080200
   */
  @Override
  public void processResource(String resource, InputStream is, List<Relocator> relocators) throws IOException {
    processResource(resource, is, relocators, 0);
  }


  /*ACR-9987432936e64c40833cd55804fe1426*/
  @Override
  public boolean canTransformResource(String resource) {
    return JarFile.MANIFEST_NAME.equalsIgnoreCase(resource);
  }


  /*ACR-53e94c742c71439a89dcb54fdf5eda58
ACR-3750f368b36745b282ecc42f86c14f60
   */
  @Override
  public boolean hasTransformedResource() {
    return true;
  }
}
