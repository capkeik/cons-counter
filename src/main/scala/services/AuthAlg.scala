package services

import cats.Functor
import dev.profunktor.redis4cats.RedisCommands
import domain.auth.{JwtToken, Password, UserName}
import pdi.jwt.JwtClaim

trait AuthAlg[F[_]] {
  def newUser(userName: UserName, password: Password): F[JwtToken]
  def login(userName: UserName, password: Password): F[JwtToken]
  def ogout(token: JwtToken, userName: UserName): F[Unit]
}

trait UserAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}
