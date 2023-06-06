package modules

import domain.auth.UserId
import domain.category.{Category, CategoryId, CreateCategory}
import domain.errors.CategoryNameInUse

trait Categories[F[_]] {
  def findAll(userId: UserId): F[List[Category]]
  def findIncomes(userId: UserId): F[List[Category]]
  def findOutcomes(userId: UserId): F[List[Category]]
  def create(userId: UserId, createCategory: CreateCategory): F[Either[CategoryNameInUse, Unit]]
  def delete(userId: UserId, categoryId: CategoryId): F[Unit]
}

