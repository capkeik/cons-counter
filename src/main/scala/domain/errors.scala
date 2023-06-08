package domain

import domain.account.{AccountId, AccountName}
import domain.auth.{UserId, UserName}
import domain.category.{CategoryId, CategoryName}
import domain.transaction.TransactionId
import io.circe.{Decoder, Encoder, HCursor, Json}
import sttp.tapir.Schema


object errors {
  sealed class AppError(
    val message: String,
    val th: Throwable = new Throwable()
  ) extends Throwable

  object AppError {
    implicit val encoder: Encoder[AppError] = new Encoder[AppError] {
      override def apply(a: AppError): Json = Json.obj(
        ("message", Json.fromString(a.message))
      )
    }

    implicit val decoder: Decoder[AppError] = new Decoder[AppError] {
      override def apply(c: HCursor): Decoder.Result[AppError] =
        c.downField("message").as[String].map(MockError(_))
    }

    implicit val schema: Schema[AppError] = Schema.string[AppError]
  }

  case class UserNotFound(username: UserName)
    extends AppError(message = s"Username ${username.value} not found")

  case class UserIdNotFound(id: UserId)
    extends AppError(message = s"User ${id.value} not found")

  case class UserNameInUse(username: UserName)
    extends AppError(message = s"Username ${username.value} already in use")

  case class InvalidPassword()
    extends AppError(message = s"Invalid password")

  case class CategoryNameInUse(name: CategoryName)
    extends AppError(message = s"Category name $name already in use")

  case class CategoryNotFound(id: CategoryId)
    extends AppError(message = s"Category \"$id\" not found")

  case class AccountNameInUse(name: AccountName)
    extends AppError(message = s"Account name $name already in use")

  case class AccountNotFound(id: AccountId)
    extends AppError(message = s"Account \"$id\" not found")

  case class InternalError(messageIn: String = "", cause: Throwable = new Throwable())
    extends AppError(message = s"$messageIn (((")

  case class TransactionNotFound(id: TransactionId, cause: Throwable = new Throwable())
    extends AppError(message = s"Transaction \"$id\" not found")

  case class MockError(override val message: String) extends AppError(message = message)
}
