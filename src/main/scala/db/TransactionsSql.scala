package db

import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import domain.auth.UserId
import domain.errors.TransactionNotFound
import domain.transaction.{CreateTransaction, Transaction, TransactionId}
import domain.{Income, Outcome, TransactionDirection}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

trait TransactionsSql {
  def listAll(userId: UserId): ConnectionIO[List[Transaction]]

  def listIncomes(userId: UserId): ConnectionIO[List[Transaction]]

  def listOutcomes(userId: UserId): ConnectionIO[List[Transaction]]

  def create(userId: UserId, createTransaction: CreateTransaction): ConnectionIO[Unit]

  def remove(userId: UserId, transactionId: TransactionId): ConnectionIO[Either[TransactionNotFound, Unit]]

  def findById(userId: UserId, transactionId: TransactionId): ConnectionIO[Option[Transaction]]
}

object TransactionsSql {

  def make = new Impl

  final class Impl extends TransactionsSql {

    import queries._

    override def listAll(userId: UserId): ConnectionIO[List[Transaction]] =
      findAllSql(userId).to[List]

    override def listIncomes(userId: UserId): ConnectionIO[List[Transaction]] =
      findByType(userId, Income).to[List]

    override def listOutcomes(userId: UserId): ConnectionIO[List[Transaction]] =
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