package db

import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import domain.{Income, Outcome, TransactionDirection}
import domain.auth.UserId
import domain.errors.{AppError, TransactionNotFound}
import domain.transaction.{CreateTransaction, Transaction, TransactionId}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

trait TransactionsSql {
  def findAll(userId: UserId): ConnectionIO[List[Transaction]]
  def findIncomes(userId: UserId): ConnectionIO[List[Transaction]]
  def findOutcomes(userId: UserId): ConnectionIO[List[Transaction]]
  def create(userId: UserId, createTransaction: CreateTransaction): ConnectionIO[Unit]
  def remove(userId: UserId, transactionId: TransactionId): ConnectionIO[Either[TransactionNotFound, Unit]]
  def findById(userId: UserId, transactionId: TransactionId): ConnectionIO[Option[Transaction]]
}

object TransactionsSql {

  def make = new Impl

  private final class Impl extends TransactionsSql {
    import queries._
    override def findAll(userId: UserId): ConnectionIO[List[Transaction]] =
      findAllSql(userId).to[List]

    override def findIncomes(userId: UserId): ConnectionIO[List[Transaction]] =
      findByType(userId, Income).to[List]

    override def findOutcomes(userId: UserId): ConnectionIO[List[Transaction]] =
      findByType(userId, Outcome).to[List]

    override def create(userId: UserId, createTransaction: CreateTransaction): ConnectionIO[Unit] =
      createSql(userId, createTransaction).run.map(
        _ => ().pure[ConnectionIO]
      )

    override def remove(userId: UserId, transactionId: TransactionId): ConnectionIO[Either[TransactionNotFound, Unit]] =
      findByIdSql(userId, transactionId).option.map {
        case None => TransactionNotFound(transactionId).asLeft[Unit]
        case _ => ().asRight[TransactionNotFound]
      }

    override def findById(userId: UserId, transactionId: TransactionId): ConnectionIO[Option[Transaction]] =
      findByIdSql(userId, transactionId).option
  }
  private object queries {
    def findAllSql(userId: UserId): Query0[Transaction] =
      sql"""
           select * from transactions
           where user_id = ${userId.value}
         """.query[Transaction]

    def findByType(userId: UserId, trType: TransactionDirection): Query0[Transaction] =
      sql"""
           select * from transactions where user_id = ${userId.value} and type = ${trType.str}
         """.query[Transaction]

    def createSql(userId: UserId, createTransaction: CreateTransaction): Update0 =
      sql"""
            insert into transactions (user_id, category, account, type, amount, name, description)
            values (
            ${userId.value},
            ${createTransaction.categoryId.value},
            ${createTransaction.accountId.value},
            ${createTransaction.transactionType.value.str},
            ${createTransaction.amount.value},
            ${createTransaction.name.value},
            ${createTransaction.transactionDescription.value})
         """.stripMargin.update

    def removeSql(userId: UserId, transactionId: TransactionId): Update0 =
      sql"""
           select * from transactions
           where user_id = ${userId.value} and id = ${transactionId.value}
         """.update

    def findByIdSql(userId: UserId, transactionId: TransactionId): Query0[Transaction] =
      sql"""
           select * from transactions
           where user_id = ${userId.value} and id = ${transactionId.value}
         """.query[Transaction]
  }
}