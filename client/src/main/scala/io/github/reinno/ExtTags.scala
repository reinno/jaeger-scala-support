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
