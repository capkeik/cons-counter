import derevo.circe.{decoder, encoder}
import derevo.derive
import io.estatico.newtype.macros.newtype

package object domain {
  @derive(decoder, encoder)
  @newtype case class Amount(value: BigDecimal)

  sealed trait TransactionDirection {
    def str: String
  }
  case object Income extends TransactionDirection {
    override def str = "income"
  }

  override case object Outcome extends TransactionDirection {
    override def str = "outcome"
  }
}
