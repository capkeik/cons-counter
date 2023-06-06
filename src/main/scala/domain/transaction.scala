package domain

import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import domain.account.{Account, AccountId}
import domain.category.{Category, CategoryId}
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.string.ValidBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation.{loggable, show}

import java.sql.Timestamp
import java.util.UUID

object transaction {
  @derive(loggable, decoder, encoder)
  @newtype case class TransactionId(value: UUID)

  @newtype case class TransactionTS(value: Long)

  @derive(decoder, encoder, eqv, show)
  @newtype case class TransactionName(value: String)

  @derive(decoder, encoder, eqv, show)
  @newtype case class TransactionDescription(value: String)

  @derive(loggable, decoder, encoder)
  case class TransactionType(value: TransactionDirection)

  @derive(decoder, encoder, eqv, show)
  case class Transaction(
    name: TransactionName,
    transactionType: TransactionType,
    transactionDescription: TransactionDescription,
    category: Category,
    account: Account,
    amount: Amount,
    timestamp: TransactionTS
  )
  @newtype
  case class TransactionNameParam(value: NonEmptyString) {
    def toDomain: TransactionName = TransactionName(value)
  }

  case class TransactionDescParam(value: NonEmptyString) {
    def toDomain: TransactionDescription = TransactionDescription(value)
  }
  @newtype
  case class TransactionTypeParam(value: NonEmptyString) {
    def toDomain: TransactionType = {
      val str = value.toLowerCase
      TransactionType(
        str match {
          case Income.str => Income
          case Outcome.str => Outcome
        }
      )
    }
  }

  @derive(decoder, encoder, show)
  @newtype
  case class AmountParam(value: String Refined ValidBigDecimal)

  @derive(decoder, encoder, show)
  case class CreateTransactionParam(
    name: TransactionNameParam,
    transactionType: TransactionTypeParam,
    description: TransactionDescParam,
    categoryId: CategoryId,
    accountId: AccountId,
    amount: AmountParam
  ) {
    def toDomain: CreateTransaction =
      CreateTransaction(
        name.toDomain,
        transactionType.toDomain,
        description.toDomain,
        categoryId,
        accountId,
        Amount(BigDecimal(amount.value))
      )
  }

  case class CreateTransaction(
    name: TransactionName,
    transactionType: TransactionType,
    transactionDescription: TransactionDescription,
    categoryId: CategoryId,
    accountId: AccountId,
    amount: Amount
  )
}
