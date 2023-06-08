package domain

import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import domain.account.{Account, AccountId}
import domain.category.{Category, CategoryId}
import doobie.Read
import eu.timepit.refined.api.Refined
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.string.ValidBigDecimal
import eu.timepit.refined.types.string.NonEmptyString
import io.circe.{Decoder, Encoder, Json}
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation.{loggable, show}

import java.sql.Timestamp
import java.util.UUID

object transaction {
  @derive(loggable, decoder, encoder)
  @newtype case class TransactionId(value: Long)
  object TransactionId {
    implicit val doobieRead: Read[TransactionId] = Read[Long].map(TransactionId(_))
  }
  @newtype case class TransactionTS(value: Long)

  object TransactionTS {
    implicit val doobieRead: Read[TransactionTS] = Read[Long].map(TransactionTS(_))
    implicit val decoder: Decoder[TransactionTS] =
      Decoder[Long].map(TransactionTS(_))

    implicit val encoder: Encoder[TransactionTS] =
      new Encoder[TransactionTS] {
        override def apply(a: TransactionTS): Json =
          Json.fromLong(a.value)
      }
  }
  @derive(decoder, encoder)
  @newtype case class TransactionName(value: String)
  object TransactionName {
    implicit val doobieRead: Read[TransactionName] = Read[String].map(TransactionName(_))
  }

  @derive(decoder, encoder)
  @newtype case class TransactionDescription(value: String)
  object TransactionDescription {
    implicit val doobieRead: Read[TransactionDescription] = Read[String].map(TransactionDescription(_))
  }

  @derive(decoder, encoder)
  case class TransactionType(value: TransactionDirection)
  object TransactionType {
    implicit val doobieRead: Read[TransactionType] = Read[TransactionDirection].map(TransactionType(_))
  }

  @derive(decoder, encoder)
  case class Transaction(
    name: TransactionName,
    transactionType: TransactionType,
    transactionDescription: TransactionDescription,
    category: Category,
    account: Account,
    amount: Amount,
    timestamp: TransactionTS
  )
//  @newtype
//  case class TransactionNameParam(value: NonEmptyString) {
//    def toDomain: TransactionName = TransactionName(value)
//  }
//
//  case class TransactionDescParam(value: NonEmptyString) {
//    def toDomain: TransactionDescription = TransactionDescription(value)
//  }
//  @newtype
//  case class TransactionTypeParam(value: NonEmptyString) {
//    def toDomain: TransactionType = {
//      val str = value.toLowerCase
//      TransactionType(
//        str match {
//          case Income.str => Income
//          case Outcome.str => Outcome
//        }
//      )
//    }
//  }
//
//  @derive(decoder, encoder, show)
//  @newtype
//  case class AmountParam(value: String Refined ValidBigDecimal)
//
//  @derive(decoder, encoder, show)
//  case class CreateTransactionParam(
//    name: TransactionNameParam,
//    transactionType: TransactionTypeParam,
//    description: TransactionDescParam,
//    categoryId: CategoryId,
//    accountId: AccountId,
//    amount: AmountParam
//  ) {
//    def toDomain: CreateTransaction =
//      CreateTransaction(
//        name.toDomain,
//        transactionType.toDomain,
//        description.toDomain,
//        categoryId,
//        accountId,
//        Amount(BigDecimal(amount.value))
//      )
//  }

  case class CreateTransaction(
    name: TransactionName,
    transactionType: TransactionType,
    transactionDescription: TransactionDescription,
    categoryId: CategoryId,
    accountId: AccountId,
    amount: Amount
  )
}
