/*
 * Copyright (C) 2021 The Dagger Authors.
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

package dagger.hilt.processor.internal.root;

import androidx.room.compiler.processing.XFiler.Mode;
import androidx.room.compiler.processing.XTypeElement;
import com.squareup.javapoet.AnnotationSpec;
import dagger.hilt.processor.internal.ClassNames;
import dagger.hilt.processor.internal.Processors;
import java.io.IOException;

/** Generates an {@link dagger.hilt.internal.processedrootsentinel.ProcessedRootSentinel}. */
final class ProcessedRootSentinelGenerator {
  private final XTypeElement processedRoot;
  private final Mode mode;

  ProcessedRootSentinelGenerator(XTypeElement processedRoot, Mode mode) {
    this.processedRoot = processedRoot;
    this.mode = mode;
  }

  void generate() throws IOException {
    Processors.generateAggregatingClass(
        ClassNames.PROCESSED_ROOT_SENTINEL_PACKAGE,
        AnnotationSpec.builder(ClassNames.PROCESSED_ROOT_SENTINEL)
            .addMember("roots", "$S", processedRoot.getQualifiedName())
            .build(),
        processedRoot,
        getClass(),
        mode);
  }
}
