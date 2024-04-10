/*
 * Copyright (C) 2020 The Dagger Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dagger.android.internal.proguard;

import androidx.room.compiler.processing.XProcessingEnv;
import androidx.room.compiler.processing.XProcessingStep;
import androidx.room.compiler.processing.javac.JavacBasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableList;
import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

/**
 * An {@linkplain Processor annotation processor} to generate dagger-android's specific proguard
 * needs. This is only intended to run over the dagger-android project itself, as the alternative is
 * to create an intermediary java_library for proguard rules to be consumed by the project.
 *
 * <p>Basic structure looks like this:
 *
 * <pre><code>
 *   resources/META-INF/com.android.tools/proguard/dagger-android.pro
 *   resources/META-INF/com.android.tools/r8/dagger-android.pro
 *   resources/META-INF/proguard/dagger-android.pro
 * </code></pre>
 */
@AutoService(Processor.class)
public final class ProguardProcessor extends JavacBasicAnnotationProcessor {
  private XProcessingEnv env;

  @Override
  public void initialize(XProcessingEnv env) {
    this.env = env;
  }

  @Override
  public Iterable<XProcessingStep> processingSteps() {
    return ImmutableList.of(new ProguardProcessingStep(env));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
