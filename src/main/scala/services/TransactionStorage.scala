package services

import cats.syntax.applicativeError._
import cats.syntax.either._
import config.RequestContext
import config.types.IOWithContext
import db.TransactionsSql
import domain.auth.UserId
import domain.errors.{AppError, InternalError, TransactionNotFound}
import domain.transaction.{CreateTransaction, Transaction, TransactionId}
import doobie.Transactor
import doobie.implicits._
import logs.surroundWithLogs
import tofu.logging.Logging
import tofu.logging.derivation.loggable.generate


trait TransactionStorage {
  def list: IOWithContext[Either[AppError, List[Transaction]]]

  def listOutcome: IOWithContext[Either[AppError, List[Transaction]]]

  def listIncome: IOWithContext[Either[AppError, List[Transaction]]]

  def create(transaction: CreateTransaction): IOWithContext[Either[AppError, Unit]]

  def removeById(id: TransactionId): IOWithContext[Either[TransactionNotFound, Unit]]
}

object TransactionStorage {
  private final class Impl(
    userId: UserId,
    sql: TransactionsSql,
    transactor: Transactor[IOWithContext]
  ) extends TransactionStorage {
    override def list: IOWithContext[Either[AppError, List[Transaction]]] =
      sql.listAll(userId).transact(transactor).attempt.map(_.leftMap(InternalError("", _)))

    override def listOutcome: IOWithContext[Either[AppError, List[Transaction]]] =
      sql.listOutcomes(userId).transact(transactor).attempt.map(_.leftMap(InternalError("", _)))

    override def listIncome: IOWithContext[Either[AppError, List[Transaction]]] =
      sql.listIncomes(userId).transact(transactor).attempt.map(_.leftMap(InternalError("", _)))

    override def create(transaction: CreateTransaction): IOWithContext[Either[AppError, Unit]] =
      sql.create(userId, transaction).transact(transactor).attempt.map(
        _.leftMap(InternalError("", _))
      )

    override def removeById(id: TransactionId): IOWithContext[Either[TransactionNotFound, Unit]] =
      sql.remove(userId, id).transact(transactor).attempt.map {
        case Left(th) => TransactionNotFound(id, th).asLeft[Unit]
        case Right(Left(error)) => error.asLeft[Unit]
        case _ => ().asRight[TransactionNotFound]
      }
  }

  private final class LoggingImpl(storage: TransactionStorage)(
    implicit logging: Logging[IOWithContext]
  ) extends TransactionStorage {
    override def list: IOWithContext[Either[AppError, List[Transaction]]] =
      surroundWithLogs[AppError, List[Transaction]]("Getting all transactions") {
        error =>
          (s"Error while getting all transactions: ${error.message}", None)
      } { result =>
        s"All accounts: ${result.mkString}"
      }(storage.list)

    override def listOutcome: IOWithContext[Either[AppError, List[Transaction]]] =
      surroundWithLogs[AppError, List[Transaction]]("Getting all outcomes") {
        error =>
          (s"Error while getting all outcomes: ${error.message}", None)
      } { result =>
        s"All accounts: ${result.mkString}"
      }(storage.listOutcome)

    override def listIncome: IOWithContext[Either[AppError, List[Transaction]]] =
      surroundWithLogs[AppError, List[Transaction]]("Getting all incomes") {
        error =>
          (s"Error while getting all incomes: ${error.message}", None)
      } { result =>
        s"All accounts: ${result.mkString}"
      }(storage.listIncome)

    override def create(transaction: CreateTransaction): IOWithContext[Either[AppError, Unit]] =
      surroundWithLogs[AppError, Unit](s"Creating transaction with params $transaction") {
        error => (s"Error while creating transaction: ${error.message}", None)
      } { transaction =>
        s"Created transaction $transaction"
      }(storage.create(transaction))

    override def removeById(id: TransactionId): IOWithContext[Either[TransactionNotFound, Unit]] =
      surroundWithLogs[TransactionNotFound, Unit](s"Removing transaction by id ${id.value}") {
        error => (s"Error while removing account: ${error.message}", None)
      } { _ =>
        s"Removed transaction with id ${id.value}"
      }(storage.removeById(id))

    def make(
      id: UserId,
      sql: TransactionsSql,
      transactor: Transactor[IOWithContext]
    ): TransactionStorage = {
        Logging.Make
          .contextual[IOWithContext, RequestContext]
          .forService[TransactionStorage]
      val storage = new Impl(id, sql, transactor)
      new LoggingImpl(storage)
    }
  }
}