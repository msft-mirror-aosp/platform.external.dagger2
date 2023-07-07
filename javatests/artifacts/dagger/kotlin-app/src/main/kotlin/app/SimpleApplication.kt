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

package app

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject
import javax.inject.Singleton
import library.MySubcomponent

/** A simple, skeletal application that defines a simple component. */
class SimpleApplication {
  class Foo @Inject constructor()

  @Module
  object SimpleModule {
    @Provides
    fun provideFoo(): Foo {
      return Foo()
    }
  }

  @Singleton
  @Component(modules = [SimpleModule::class])
  interface SimpleComponent {
    fun foo(): Foo

    // Reproduces a regression in https://github.com/google/dagger/issues/2997.
    fun mySubcomponentFactory(): MySubcomponent.Factory
  }

  companion object {
    fun main() {
      val foo: Foo = DaggerSimpleApplication_SimpleComponent.create().foo()
    }
  }
}

fun main() {
  SimpleApplication.main()
  AssistedInjects.main()
}
