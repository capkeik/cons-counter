package domain

import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.Read
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto._
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.Decoder
import io.estatico.newtype.macros.newtype
//import io.circe.{Decoder, Encoder}
//import io.circe.generic.semiauto._
//import io.circe.syntax._
import io.circe.refined._
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, Schema}
import tofu.logging.derivation.{loggable, show}

object account {
  @derive(loggable, decoder, encoder)
  @newtype case class AccountId(value: Long)
  object AccountId {
    implicit val doobieRead: Read[AccountId] = Read[Long].map(AccountId(_))
    implicit val schema: Schema[AccountId] =
      Schema.schemaForLong.map(l => Some(AccountId(l)))(_.value)
    implicit val codec: Codec[String, AccountId, TextPlain] =
      Codec.long.map(AccountId(_))(_.value)
  }

  @derive(decoder, encoder, show)
  @newtype case class AccountName(value: String)
  object AccountName {
    implicit val doobieRead: Read[AccountName] = Read[String].map(AccountName(_))
    implicit val schema: Schema[AccountName] =
      Schema.schemaForString.map(n => Some(AccountName(n)))(_.value)
  }

  @derive(decoder, encoder)
  case class Account(
    id: AccountId,
    name: AccountName,
    amount: Amount
  )

  @derive(decoder, encoder)
  @newtype
  case class AccountNameParam(value: String)

  @derive(decoder, encoder)
  @newtype
  case class AmountParam(value: BigDecimal)
  object AmountParam

  @derive(decoder, encoder)
  case class CreateAccountParam(
    name: AccountNameParam,
    amount: AmountParam
  ) {
    def toDomain: CreateAccount =
      CreateAccount(
        AccountName(name.value),
        Amount(amount.value)
      )
  }

  case class CreateAccount(
    name: AccountName,
    amount: Amount
  )

  @derive(decoder, encoder)
  @newtype
  case class AccountIdParam(value: Long)
  @derive(decoder, encoder)
  case class UpdateAccountParam(
    id: AccountIdParam,
    amount: AmountParam
  ) {
    def toDomain: UpdateAccount =
      UpdateAccount(
        AccountId(id.value),
        Amount(amount.value)
      )
  }

  @derive(decoder, encoder)
  case class UpdateAccount(
    id: AccountId,
    amount: Amount
  )
}
