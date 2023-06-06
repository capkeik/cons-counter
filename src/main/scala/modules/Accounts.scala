package modules

import domain.Amount
import domain.account.{Account, AccountId, AccountName, CreateAccount, UpdateAccount}
import domain.auth.UserId

trait Accounts[F[_]] {
  def findAll(userId: UserId): F[List[Account]]
  def create(userId: UserId, account: CreateAccount): F[AccountId]
  def update(accountId: AccountId, account: UpdateAccount): F[Unit]
  def delete(accountId: AccountId): F[Unit]
}
