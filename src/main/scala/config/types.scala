package config

import cats.data.ReaderT
import cats.effect.IO
import derevo.derive
import sttp.tapir.Codec
import sttp.tapir.CodecFormat.TextPlain
import tofu.logging.derivation.loggable

object types {
  type IOWithContext[A] = ReaderT[IO, RequestContext, A]
}

@derive(loggable)
final case class RequestContext(requestId: String)
object RequestContext {
  implicit val codec: Codec[String, RequestContext, TextPlain] =
    Codec.string.map(RequestContext(_))(_.requestId)
}