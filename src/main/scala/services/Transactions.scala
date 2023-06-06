package services

import domain.auth.UserId
import domain.transaction.{CreateTransaction, Transaction, TransactionId}

trait Transactions[F[_]] {
  def findAll(userId: UserId): F[List[Transaction]]
  def create(userId: UserId, transaction: CreateTransaction): F[TransactionId]
  def delete(transactionId: TransactionId): F[Unit]
}
