package modules

import domain.auth.{EncryptedPassword, User, UserId, UserName}

trait Users[F[_]] {
  def find(username: UserName): F[Option[User]]
  def create(username: UserName, password: EncryptedPassword): F[UserId]
}
