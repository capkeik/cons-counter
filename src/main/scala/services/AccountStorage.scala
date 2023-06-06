package services

import cats.Id
import cats.syntax.applicativeError._
import cats.syntax.either._
import config.RequestContext
import config.types.IOWithContext
import db.AccountSql
import domain.account.{Account, AccountId, CreateAccount}
import domain.auth.UserId
import domain.errors.{AppError, InternalError}
import doobie.Transactor
import doobie.implicits._
import logs.surroundWithLogs
import tofu.logging.Logging
import tofu.logging.derivation.loggable.generate

trait AccountStorage {
  def list: IOWithContext[Either[InternalError, List[Account]]]

  def create(account: CreateAccount): IOWithContext[Either[AppError, Unit]]

  def removeById(id: AccountId): IOWithContext[Either[AppError, Unit]]
}

object AccountStorage {


  def make(
    id: UserId,
    sql: AccountSql,
    transactor: Transactor[IOWithContext]
  ): AccountStorage = {
    implicit val logs: Id[Logging[IOWithContext]] =
      Logging.Make
        .contextual[IOWithContext, RequestContext]
        .forService[AccountStorage]
    val storage = new Impl(id, sql, transactor)
    new LoggingImpl(storage)
  }

  private final class Impl(
    userId: UserId,
    sql: AccountSql,
    transactor: Transactor[IOWithContext]
  ) extends AccountStorage {
    override def list: IOWithContext[Either[InternalError, List[Account]]] =
      sql.listAll(userId).transact(transactor).attempt.map(_.leftMap(InternalError("", _)))

    override def create(account: CreateAccount): IOWithContext[Either[AppError, Unit]] =
      sql.create(userId, account).transact(transactor).attempt
        .map {
          case Left(th) => InternalError("", th).asLeft[Unit]
          case Right(Left(error)) => error.asLeft[Unit]
          case Right(Right(account)) => account.asRight[AppError]
        }

    override def removeById(id: AccountId): IOWithContext[Either[AppError, Unit]] =
      sql.remove(userId, id).transact(transactor).attempt.map {
        case Left(th) => InternalError("", th).asLeft[Unit]
        case Right(Left(error)) => error.asLeft[Unit]
        case _ => ().asRight[AppError]
      }
  }

  private final class LoggingImpl(storage: AccountStorage)(
    implicit logging: Logging[IOWithContext]
  ) extends AccountStorage {

    override def list: IOWithContext[Either[InternalError, List[Account]]] =
      surroundWithLogs[InternalError, List[Account]]("Getting all transactions") {
        error =>
          (s"Error while getting all accounts: ${error.message}", None)
      } { result =>
        s"All accounts: ${result.mkString}"
      }(storage.list)

    override def create(account: CreateAccount): IOWithContext[Either[AppError, Unit]] =
      surroundWithLogs[AppError, Unit](s"Creating account with params $account") {
        error => (s"Error while creating account: ${error.message}", None)
      } { account =>
        s"Created account $account"
      }(storage.create(account))

    override def removeById(id: AccountId): IOWithContext[Either[AppError, Unit]] =
      surroundWithLogs[AppError, Unit](s"Removing account by id ${id.value}") {
        error => (s"Error while removing account: ${error.message}", None)
      } { _ =>
        s"Removed account with id ${id.value}"
      }(storage.removeById(id))
  }
}


