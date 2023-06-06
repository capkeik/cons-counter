package domain

import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.string.{Uuid, ValidBigDecimal}
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation.{loggable, show}

import java.util.UUID

object account {
  @derive(loggable, decoder, encoder)
  @newtype case class AccountId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class AccountName(value: String)

  @derive(decoder, encoder, eqv, show)
  case class Account(
    id: AccountId,
    name: AccountName,
    amount: Amount
  )

  @derive(decoder, encoder, show)
  @newtype
  case class AccountNameParam(value: NonEmptyString)

  @derive(decoder, encoder, show)
  @newtype
  case class AmountParam(value: String Refined ValidBigDecimal)

  @derive(decoder, encoder, show)
  case class CreateAccountParam(
    name: AccountNameParam,
    amount: AmountParam
  ) {
    def toDomain: CreateAccount =
      CreateAccount(
        AccountName(name.value),
        Amount(BigDecimal(amount.value))
      )
  }

  case class CreateAccount(
    name: AccountName,
    amount: Amount
  )

  @derive(decoder, encoder)
  @newtype
  case class AccountIdParam(value: String Refined Uuid)

  @derive(decoder, encoder)
  case class UpdateAccountParam(
    id: AccountIdParam,
    amount: AmountParam
  ) {
    def toDomain(): UpdateAccount =
      UpdateAccount(
        AccountId(UUID.fromString(id.value)),
        Amount(BigDecimal(amount.value))
      )
  }

  @derive(decoder, encoder)
  case class UpdateAccount(
    id: AccountId,
    price: Amount
  )
}
