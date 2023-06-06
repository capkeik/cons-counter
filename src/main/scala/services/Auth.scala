package services

import domain.auth.{JwtToken, Password, UserName}
import pdi.jwt.JwtClaim

trait AuthAlg[F[_]] {
  def newUser(userName: UserName, password: Password): F[JwtToken]
  def login(userName: UserName, password: Password): F[JwtToken]
  def logout(token: JwtToken, userName: UserName): F[Unit]
}

trait UserAuth[F[_], A] {
  def findUser(token: JwtToken)(claim: JwtClaim): F[Option[A]]
}
