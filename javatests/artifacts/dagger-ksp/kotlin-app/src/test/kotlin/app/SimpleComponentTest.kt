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

package app

import app.SimpleComponentClasses.SimpleComponent
import com.google.common.truth.Truth.assertThat
import library.InstanceType
import library.MySubcomponent
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SimpleComponentTest {
  private lateinit var component: SimpleComponent

  @Before
  fun setUp() {
    component = DaggerSimpleComponentClasses_SimpleComponent.create()
  }

  @Test
  fun fooTest() {
    assertThat(component.foo()).isNotNull()
    assertThat(component.foo()).isNotEqualTo(component.foo())
  }

  @Test
  fun scopedFooTest() {
    assertThat(component.scopedFoo()).isNotNull()
    assertThat(component.scopedFoo()).isEqualTo(component.scopedFoo())
    assertThat(component.scopedFoo()).isEqualTo(component.scopedFooProvider().get())
  }

  @Test
  fun providedFooTest() {
    assertThat(component.providedFoo()).isNotNull()
    assertThat(component.providedFoo()).isNotEqualTo(component.providedFoo())
  }

  @Test
  fun scopedProvidedFooTest() {
    assertThat(component.scopedProvidedFoo()).isNotNull()
    assertThat(component.scopedProvidedFoo()).isEqualTo(component.scopedProvidedFoo())
    assertThat(component.scopedProvidedFoo()).isEqualTo(component.scopedProvidedFooProvider().get())
  }

  @Test
  fun subcomponentTest() {
    val instanceType = InstanceType()
    val subcomponent = component.mySubcomponentFactory().create(instanceType)
    assertThat(subcomponent).isNotNull()
    assertThat(subcomponent.instance()).isEqualTo(instanceType)
  }
}
