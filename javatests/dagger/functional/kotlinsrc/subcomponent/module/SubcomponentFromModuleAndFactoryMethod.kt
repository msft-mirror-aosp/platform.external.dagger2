/*
 * Copyright (C) 2023 The Dagger Authors.
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

package dagger.functional.kotlinsrc.subcomponent.module

import dagger.Component
import dagger.Module
import dagger.Subcomponent

/**
 * Tests for [Subcomponent]s which are defined with [Module.subcomponents] and are also requested as
 * component factory methods.
 */
class SubcomponentFromModuleAndFactoryMethod {
  @Subcomponent
  interface Sub {
    @Subcomponent.Builder
    interface Builder {
      fun sub(): Sub
    }
  }

  @Module(subcomponents = [Sub::class]) internal inner class ModuleWithSubcomponent

  @Component(modules = [ModuleWithSubcomponent::class])
  interface ExposesBuilder {
    fun subcomponentBuilder(): Sub.Builder
  }
}
