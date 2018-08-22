/*
 * Copyright 2018 the jaeger scala support contributors. See AUTHORS for more details.
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
 *
 */

package io.github.reinno

import io.opentracing.tag.StringTag

object ExtTags {
  val HTTP_REQUEST = new StringTag("http.request")
  val HTTP_RESPONSE = new StringTag("http.response")
  val HTTP_STATUS_CODE = new StringTag("http.status.code")
  val SERVICE_INSTANCE = new StringTag("service.instance")
  val VERSION = new StringTag("version")
  val EXCEPTION = new StringTag("exception")
}
