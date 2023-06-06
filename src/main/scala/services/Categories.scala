package services

import domain.auth.UserId
import domain.category.{Category, CategoryId, CreateCategory}

trait Categories[F[_]] {
  def findAll(userId: UserId): F[List[Category]]
  def findIncomes(userId: UserId): F[List[Category]]
  def findOutcomes(userId: UserId): F[List[Category]]
  def create(userId: UserId, createCategory: CreateCategory): F[CategoryId]
  def delete(userId: UserId, categoryId: CategoryId): F[Unit]
}

