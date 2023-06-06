package domain

import domain.account.AccountName
import domain.auth.UserName
import domain.category.CategoryName

import scala.util.control.NoStackTrace

object errors {
  sealed abstract class AppError(
    val message: String,
    val cause: Option[Throwable] = None
  )

  case class UserNotFound(username: UserName)
    extends AppError(message = s"Username $username not found")

  case class UserNameInUse(username: UserName)
    extends AppError(message = s"Username $username already in use")

  case class InvalidPassword()
    extends AppError(message = s"Invalid password")

  case object TokenNotFound extends AppError(message = s"Token not found")

  case class CategoryNameInUse(name: CategoryName)
    extends AppError(message = s"Category name $name already in use")

  case class CategoryNotFound(name: CategoryName)
    extends AppError(message = s"Category \"$name\" not found")

  case class AccountNameInUse(name: AccountName)
    extends AppError(message = s"Account name $name already in use")

  case class AccountNotFound(name: AccountName)
    extends AppError(message = s"Account \"$name\" not found")
}
