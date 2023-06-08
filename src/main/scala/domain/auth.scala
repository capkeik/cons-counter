package domain

import cats.Show
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.Read
import eu.timepit.refined.types.string.NonEmptyString
import eu.timepit.refined.auto._
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation._

import java.util.UUID
import javax.crypto.Cipher
import scala.util.control.NoStackTrace

object auth {

  @derive(loggable, encoder, decoder)
  @newtype case class UserId(value: Long)
  object UserId {
    implicit val doobieRead: Read[UserId] =
      Read[Long].map(UserId(_))

    implicit val showId: Show[UserId] =
      Show.show(id => id.value.toString)
  }

  @derive(loggable, encoder, decoder)
  @newtype case class UserName(value: String)
  object UserName {
    implicit val doobieRead: Read[UserName] =
      Read[String].map(UserName(_))

    implicit val showId: Show[UserName] =
      Show.show(name => name.value)
  }

  @derive(loggable, encoder, decoder)
  @newtype case class Password(value: String)
  object Password {
    implicit val doobieRead: Read[Password] =
      Read[String].map(Password(_))
  }

//  @derive(loggable, encoder, decoder)
//  @newtype case class JwtToken(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class EncryptedPassword(value: String)

  @newtype case class EncryptCipher(value: Cipher)

  @newtype case class DecryptCipher(value: Cipher)

  @derive(encoder, decoder, show)
  case class User(uuid: UserId, name: UserName)

  case class UserWithPassword(uuid: UserId, name: UserName, password: Password)

  case class CreateUser(
    name: UserName,
    password: Password
  )

  case class UserNotFound(username: UserName) extends NoStackTrace
  case class UserNameInUse(username: UserName) extends NoStackTrace
  case class InvalidPassword(username: UserName) extends NoStackTrace
  case object TokenNotFound extends NoStackTrace

  case object UnsupportedOperation extends NoStackTrace

  @derive(decoder, encoder)
  case class LoginUser(
    username: UserName,
    password: Password
  )
}
