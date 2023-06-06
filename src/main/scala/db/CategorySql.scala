package db

import cats.syntax.either._
import domain.{Income, Outcome, TransactionDirection}
import domain.auth.UserId
import domain.category.{Category, CategoryId, CategoryName, CreateCategory}
import domain.errors.CategoryNotFound
import domain.transaction.TransactionType
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

trait CategorySql {
  def listAll(userId: UserId): ConnectionIO[List[Category]]
  def listOutcomes(userId: UserId, catType: TransactionType): ConnectionIO[List[Category]]
  def listIncomes(userId: UserId, catType: TransactionType): ConnectionIO[List[Category]]
  def createCat(userId: UserId, createCategory: CreateCategory): ConnectionIO[Category]
  def removeCat(userId: UserId, categoryId: CategoryId): ConnectionIO[Either[CategoryNotFound, Unit]]
}
Category
object CategorySql {
  object queries {
    def listAllSql(userId: UserId): Query0[Category] =
      sql"""
           select * from category where user_id = ${userId.value}
         """.query[Category]

    def listByType(userId: UserId, catType: TransactionDirection): Query0[Category] =
      sql"""
            select * from category where user_id = ${userId.value} and type = ${catType.str}
         """.query[Category]

    def createCatSql(userId: UserId, createCategory: CreateCategory): Update0 =
      sql"""
            insert into category (user_id, name, type)
            values (${userId.value}, ${createCategory.name.value}, ${createCategory.categoryType.value.str})
         """.update

    def removeCatSql(userId: UserId, categoryId: CategoryId): Update0 =
      sql"""
            delete from category where id = ${categoryId.value}
         """.update

    def findByIdSql(userId: UserId, categoryId: CategoryId): Query0[Option[Category]] =
      sql"""
           select * from account
           where user_id = ${userId} and id = ${categoryId}
         """.query[Option[Category]]

    def findByNameSql(userId: UserId, categoryName: CategoryName): Query0[Option[Category]] =
      sql"""
           select * from account
           where user_id = ${userId} and name = ${categoryName}
         """.query[Option[Category]]

  }

  private final class Impl extends CategorySql {

    import queries._

    override def listAll(userId: UserId): ConnectionIO[List[Category]] =
      listAllSql(userId).to[List]

    override def listOutcomes(userId: UserId, catType: TransactionType): ConnectionIO[List[Category]] =
      listByType(userId, Outcome).to[List]

    override def listIncomes(userId: UserId, catType: TransactionType): ConnectionIO[List[Category]] =
      listByType(userId, Income).to[List]

    override def createCat(
      userId: UserId,
      createCategory: CreateCategory
    ): doobie.ConnectionIO[Category] =
      createCatSql(userId, createCategory).withUniqueGeneratedKeys[CategoryId]("id")
        .map(id =>
          Category(id, createCategory.name, createCategory.categoryType)
        )

    override def removeCat(userId: UserId, categoryId: CategoryId): ConnectionIO[Either[CategoryNotFound, Unit]] =
      removeCatSql(userId, categoryId).run.map {
        case 0 => CategoryNotFound(categoryId).asLeft[Unit]
        case _ => ().asRight[CategoryNotFound]
      }
  }
}