import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.Read
import doobie.util.{Get, Put}
import io.circe.{Decoder, Encoder, Json}
import io.estatico.newtype.macros.newtype
import sttp.tapir.Schema

package object domain {
  @derive(decoder, encoder)
  @newtype case class Amount(value: BigDecimal)
  object Amount {
    implicit val doobieRead: Read[Amount] = Read[BigDecimal].map(Amount(_))
    implicit val schema: Schema[Amount] =
      Schema.schemaForBigDecimal.map(
        bd => Some(Amount(bd))
      )(_.value)
  }

  sealed trait TransactionDirection {
    def str: String
  }
  case object Income extends TransactionDirection {
    override def str = "income"
  }

  case object Outcome extends TransactionDirection {
    override def str = "outcome"
  }
  object TransactionDirection {
    implicit val encodeType: Encoder[TransactionDirection] = {
      case a@Income => Json.obj(("str", Json.fromString(a.str)))
      case a@Outcome => Json.obj(("str", Json.fromString(a.str)))
    }
    implicit val decodeType: Decoder[TransactionDirection] =
      Decoder[String].emap {
        case "income" => Right(Income)
        case "outcome" => Right(Outcome)
        case _ => Left("Unsupported transaction type")
    }

    implicit val doobieGet : Get[TransactionDirection] =
      Get[String].map[TransactionDirection](x =>
        if(x == "income") Income else Outcome
      )

    implicit val doobiePut : Put[TransactionDirection] =
      Put[String].contramap[TransactionDirection](x =>
        if (x == Income) "income" else "outcome"
      )
  }
}
