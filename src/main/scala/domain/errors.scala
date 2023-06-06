package domain

import domain.account.{AccountId, AccountName}
import domain.auth.{UserId, UserName}
import domain.category.{CategoryId, CategoryName}
import domain.transaction.TransactionId

import scala.util.control.NoStackTrace

object errors {
  sealed abstract class AppError(
    val message: String,
  ) extends Throwable

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

  case class InternalError(messageIn: String)
    extends AppError(message = s"$messageIn (((")

  case class TransactionNotFound(id: TransactionId)
    extends AppError(message = s"Transaction \"$id\" not found")
}
