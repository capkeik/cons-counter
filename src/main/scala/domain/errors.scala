package domain

import domain.account.{AccountId, AccountName}
import domain.auth.UserName
import domain.category.{CategoryId, CategoryName}

import scala.util.control.NoStackTrace

object errors {
  sealed abstract class AppError(
    val message: String,
  ) extends Throwable

  case class UserNotFound(username: UserName)
    extends AppError(message = s"Username $username not found")

  case class UserNameInUse(username: UserName)
    extends AppError(message = s"Username $username already in use")

  case class InvalidPassword()
    extends AppError(message = s"Invalid password")

  case object TokenNotFound extends AppError(message = s"Token not found")

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
}
