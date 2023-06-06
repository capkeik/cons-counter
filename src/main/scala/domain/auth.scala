package domain

import derevo.circe.{decoder, encoder}
import derevo.derive
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

  @derive(loggable, encoder, decoder)
  @newtype case class UserName(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class Password(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class JwtToken(value: String)

  @derive(loggable, encoder, decoder)
  @newtype case class EncryptedPassword(value: String)

  @newtype case class EncryptCipher(value: Cipher)

  @newtype case class DecryptCipher(value: Cipher)

  @derive(decoder, encoder)
  @newtype
  case class UserNameParam(value: NonEmptyString) {
    def toDomain: UserName = UserName(value.toLowerCase())
  }

  @derive(encoder, decoder, show)
  case class User(uuid: UserId, name: UserName)

  case class UserWithPassword(uuid: UserId, name: UserName, password: Password)

  @derive(decoder, encoder)
  @newtype
  case class PasswordParam(value: NonEmptyString) {
    def toDomain: Password = Password(value)
  }

  @derive(decoder, encoder)
  case class CreateUserParam(
    userName: UserNameParam,
    password: PasswordParam
  ) {
    def toDomain: CreateUser = CreateUser(
      userName.toDomain,
      password.toDomain
    )
  }

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
    username: UserNameParam,
    password: PasswordParam
  )
}
