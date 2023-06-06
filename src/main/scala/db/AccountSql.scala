package db

import cats.implicits.catsSyntaxApplicativeId
import cats.syntax.either._
import domain.Amount
import domain.account.{Account, AccountId, AccountName, CreateAccount, UpdateAccount}
import domain.auth.UserId
import domain.errors.{AccountNameInUse, AccountNotFound}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

trait AccountSql {
  def listAll(userId: UserId): ConnectionIO[List[Account]]
  def create(userId: UserId, createAccount: CreateAccount): ConnectionIO[Either[AccountNameInUse, Unit]]
  def remove(userId: UserId, accountId: AccountId): ConnectionIO[Either[AccountNotFound, Unit]]
  def update(userId: UserId, updateAccount: UpdateAccount): ConnectionIO[Either[AccountNotFound, Unit]]
  def between(userId: UserId, fromAccount: AccountId, toAccount: AccountId, amount: Amount): ConnectionIO[Either[AccountNotFound, Unit]]
  def findById(userId: UserId, accountId: AccountId): ConnectionIO[Option[Account]]
  def findByName(userId: UserId, accountName: AccountName): ConnectionIO[Option[Account]]
}

object AccountSql {
  private final class Impl() extends AccountSql {
    import queries._
    override def listAll(userId: UserId): ConnectionIO[List[Account]] =
      listAllSql(userId).to[List]

    override def create(userId: UserId, createAccount: CreateAccount): ConnectionIO[Either[AccountNameInUse, Unit]] =
      findByNameSql(userId, createAccount.name).option.flatMap {
        case None =>
          createSql(userId, createAccount)
            .withUniqueGeneratedKeys[AccountId]("id")
            .map(_ =>
              ().asRight[AccountNameInUse]
            )
        case Some(_) => AccountNameInUse(createAccount.name).asLeft[Unit].pure[ConnectionIO]
      }

    override def remove(userId: UserId, accountId: AccountId): ConnectionIO[Either[AccountNotFound, Unit]] =
      removeSql(userId, accountId).run.map {
        case 0 => AccountNotFound(accountId).asLeft[Unit]
        case _ => ().asRight[AccountNotFound]
      }

    override def update(userId: UserId, updateAccount: UpdateAccount): ConnectionIO[Either[AccountNotFound, Unit]] =
      updateSql(userId, updateAccount).run.map(_ => ().asRight[AccountNotFound])

    override def findById(userId: UserId, accountId: AccountId): ConnectionIO[Option[Account]] =
      findByIdSql(userId, accountId).option

    override def findByName(userId: UserId, accountName: AccountName): ConnectionIO[Option[Account]] =
      findByNameSql(userId, accountName).option

    override def between(
      userId: UserId,
      fromAccount: AccountId,
      toAccount: AccountId,
      amount: Amount
    ): ConnectionIO[Either[AccountNotFound, Unit]] =
      findByIdSql(userId, fromAccount).option.flatMap {
        case None => AccountNotFound(fromAccount).asLeft[Unit].pure[ConnectionIO]
        case Some(from) => findByIdSql(userId, toAccount).option.flatMap {
          case None => AccountNotFound(toAccount).asLeft[Unit].pure[ConnectionIO]
          case Some(to) => {
            update(
              userId,
              UpdateAccount(
                fromAccount,
                Amount(from.amount.value - amount.value)
              )
            )
            update(
              userId,
              UpdateAccount(
                toAccount,
                Amount(to.amount.value + amount.value)
              )
            )
          }
        }
      }
  }
  object queries {
    def listAllSql(userId: UserId): Query0[Account] =
      sql"""
           select * from account
           where user_id = ${userId.value}
         """.query[Account]

    def findByNameSql(userId: UserId, accountName: AccountName): Query0[Account] =
      sql"""
           select * from account
           where user_id = ${userId} and name = ${accountName}
         """.query[Account]

    def findByIdSql(userId: UserId, accountId: AccountId): Query0[Account] =
      sql"""
           select * from account
           where user_id = ${userId} and id = ${accountId}
         """.query[Account]

    def createSql(userId: UserId, createAccount: CreateAccount): Update0 =
      sql"""
           insert into account (user_id, name, amount)
           values (${userId}, ${createAccount.name.value}, ${createAccount.amount.value})
         """.update

    def removeSql(userId: UserId, accountId: AccountId): Update0 =
      sql"""
           delete from account where user_id = ${userId} and id = ${accountId}
         """.update
    def updateSql(userId: UserId, updateAccount: UpdateAccount): Update0 =
      sql"""
           update account set (amount) = ${updateAccount.amount}
           where account.id = ${updateAccount.id} and user_id = ${userId}
         """.update
  }
}