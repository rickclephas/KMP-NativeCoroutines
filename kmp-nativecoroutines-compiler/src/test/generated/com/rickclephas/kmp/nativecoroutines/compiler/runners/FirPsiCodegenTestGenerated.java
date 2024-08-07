

package com.rickclephas.kmp.nativecoroutines.compiler.runners;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link com.rickclephas.kmp.nativecoroutines.compiler.GenerateTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("src/testData/codegen")
@TestDataPath("$PROJECT_ROOT")
public class FirPsiCodegenTestGenerated extends AbstractFirPsiCodegenTest {
  @Test
  public void testAllFilesPresentInCodegen() {
    KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("src/testData/codegen"), Pattern.compile("^(.+)\\.kt$"), Pattern.compile("^(.+)\\.fir\\.kt$"), TargetBackend.JVM_IR, true);
  }

  @Test
  @TestMetadata("annotations.kt")
  public void testAnnotations() {
    runTest("src/testData/codegen/annotations.kt");
  }

  @Test
  @TestMetadata("coroutinescope.kt")
  public void testCoroutinescope() {
    runTest("src/testData/codegen/coroutinescope.kt");
  }

  @Test
  @TestMetadata("functions.kt")
  public void testFunctions() {
    runTest("src/testData/codegen/functions.kt");
  }

  @Test
  @TestMetadata("properties.kt")
  public void testProperties() {
    runTest("src/testData/codegen/properties.kt");
  }

  @Test
  @TestMetadata("viewmodelscope.kt")
  public void testViewmodelscope() {
    runTest("src/testData/codegen/viewmodelscope.kt");
  }
}
