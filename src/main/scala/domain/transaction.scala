package domain

import derevo.circe.{decoder, encoder}
import derevo.derive
import domain.account.{Account, AccountId}
import domain.category.{Category, CategoryId}
import doobie.Read
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import io.estatico.newtype.macros.newtype
import sttp.tapir.CodecFormat.TextPlain
import sttp.tapir.{Codec, Schema}
import tofu.logging.derivation.loggable

object transaction {
  @derive(loggable, decoder, encoder)
  @newtype case class TransactionId(value: Long)
  object TransactionId {
    implicit val doobieRead: Read[TransactionId] = Read[Long].map(TransactionId(_))
    implicit val schema: Schema[TransactionId] =
      Schema.schemaForLong.map(l => Some(TransactionId(l)))(_.value)
    implicit val codec: Codec[String, TransactionId, TextPlain] =
      Codec.long.map(TransactionId(_))(_.value)
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
    implicit val schema: Schema[TransactionName] =
      Schema.schemaForString.map(n => Some(TransactionName(n)))(_.value)
    implicit val codec: Codec[String, TransactionName, TextPlain] =
      Codec.string.map(TransactionName(_))(_.value)
  }

  @derive(decoder, encoder)
  @newtype case class TransactionDescription(value: String)
  object TransactionDescription {
    implicit val doobieRead: Read[TransactionDescription] = Read[String].map(TransactionDescription(_))
    implicit val schema: Schema[TransactionDescription] =
      Schema.schemaForString.map(n => Some(TransactionDescription(n)))(_.value)
    implicit val codec: Codec[String, TransactionDescription, TextPlain] =
      Codec.string.map(TransactionDescription(_))(_.value)
  }

  @derive(decoder, encoder)
  case class TransactionType(value: TransactionDirection)
  object TransactionType {
    implicit val doobieRead: Read[TransactionType] = Read[TransactionDirection].map(TransactionType(_))
    implicit val schema: Schema[TransactionType] =
      Schema.schemaForString.map {
        case "income" => Some(TransactionType(Income))
        case "outcome" => Some(TransactionType(Outcome))
      } (_.value.str)
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

  case class CreateTransaction(
    name: TransactionName,
    transactionType: TransactionType,
    transactionDescription: TransactionDescription,
    categoryId: CategoryId,
    accountId: AccountId,
    amount: Amount
  )
  object CreateTransaction {
    implicit val schema: Schema[CreateTransaction] = Schema.derived[CreateTransaction]
    implicit val encoder: Encoder[CreateTransaction] = deriveEncoder[CreateTransaction]
//      Encoder[CreateTransaction] = (a: CreateTransaction) => Json.obj(
//      ("name", Json.fromString(a.name.value)),
//      ("transactionType", Json.fromString(a.transactionType.value.str)),
//      ("transactionDescription", Json.fromString(a.transactionDescription.value)),
//      ("categoryId", Json.fromLong(a.categoryId.value)),
//      ("accountId", Json.fromLong(a.accountId.value)),
//      ("amount", Json.fromBigDecimal(a.amount.value))
//    )
    implicit val decoder: Decoder[CreateTransaction] = deriveDecoder[CreateTransaction]
  }
}
