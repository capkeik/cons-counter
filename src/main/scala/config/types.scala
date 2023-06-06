package config

import cats.data.ReaderT
import cats.effect.IO

object types {
  type IOWithContext[A] = ReaderT[IO, RequestContext, A]
}

final case class RequestContext()