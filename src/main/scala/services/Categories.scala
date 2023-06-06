package services

import cats.effect.Resource
import cats.effect.kernel.MonadCancelThrow
import domain.auth.UserId
import domain.category.{Category, CategoryId, CategoryName, CreateCategory}
import doobie.util.transactor.Transactor

trait Categories[F[_]] {
  def findAll(userId: UserId): F[List[Category]]
  def findIncomes(userId: UserId): F[List[Category]]
  def findOutcomes(userId: UserId): F[List[Category]]
  def create(userId: UserId, createCategory: CreateCategory): F[CategoryId]
  def delete(userId: UserId, categoryId: CategoryId): F[Unit]
}

object Categories {
  def make[F[_]: MonadCancelThrow](
    transactor: Transactor[F]
  ): Categories[F] = {
    new Categories[F] {
      override def findAll(userId: UserId): F[List[Category]] = ???

      override def findIncomes(userId: UserId): F[List[Category]] = ???

      override def findOutcomes(userId: UserId): F[List[Category]] = ???

      override def create(userId: UserId, createCategory: CreateCategory): F[CategoryId] = ???

      override def delete(userId: UserId, categoryId: CategoryId): F[Unit] = ???
    }
  }
}
