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

package dagger.hilt.android.processor.internal.viewmodel

import androidx.room.compiler.processing.ExperimentalProcessingApi
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import dagger.hilt.processor.internal.BaseProcessingStep
import dagger.hilt.processor.internal.KspBaseProcessingStepProcessor

/** Annotation processor for @ViewModelInject. */
class KspViewModelProcessor(symbolProcessorEnvironment: SymbolProcessorEnvironment?) :
  KspBaseProcessingStepProcessor(symbolProcessorEnvironment) {
  @OptIn(ExperimentalProcessingApi::class)
  override fun processingStep(): BaseProcessingStep = ViewModelProcessingStep(xProcessingEnv)

  /** Provides the [KspViewModelProcessor]. */
  @AutoService(SymbolProcessorProvider::class)
  class Provider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
      return KspViewModelProcessor(environment)
    }
  }
}
