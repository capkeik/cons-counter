package db

import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import domain.auth.{CreateUser, User, UserId, UserName, UserWithPassword}
import domain.errors.{UserNameInUse, UserNotFound, UserIdNotFound}
import doobie.ConnectionIO
import doobie.implicits.toSqlInterpolator
import doobie.util.query.Query0
import doobie.util.update.Update0

trait UserSql {
  def findAll(): ConnectionIO[List[User]]
  def findById(id: UserId): ConnectionIO[Option[User]]
  def findByName(name: UserName): ConnectionIO[Option[User]]
  def findWithPass(id: UserId): ConnectionIO[Option[UserWithPassword]]
  def createUser(createUser: CreateUser): ConnectionIO[Either[UserNameInUse, Unit]]
  def deleteUser(userId: UserId): ConnectionIO[Either[UserIdNotFound, Unit]]
}

object UserSql {

  def make = new Impl

  private final class Impl extends UserSql {
    import queries._
    override def findAll(): ConnectionIO[List[User]] =
      findAllSql.to[List]

    override def findById(id: UserId): ConnectionIO[Option[User]] =
      findByIdSql(id).option

    override def findByName(name: UserName): ConnectionIO[Option[User]] =
      findByNameSql(name).option

    override def findWithPass(id: UserId): ConnectionIO[Option[UserWithPassword]] =
      findWithPassSql(id).option

    override def createUser(createUser: CreateUser): ConnectionIO[Either[UserNameInUse, Unit]] =
      findByNameSql(createUser.name).option.flatMap {
        case Some(_) => UserNameInUse(createUser.name).asLeft[Unit].pure[ConnectionIO]
        case _ => createUserSql(createUser).run.map(
          _ => ().asRight[UserNameInUse]
        )
      }

    override def deleteUser(userId: UserId): ConnectionIO[Either[UserIdNotFound, Unit]] =
      deleteUserSql(userId).run.map {
        case 0 => UserIdNotFound(userId).asLeft[Unit]
        case _ => ().asRight[UserIdNotFound]
      }
  }

  private object queries {
    def findAllSql: Query0[User] =
      sql"""
           select (id, name) from users
         """.query[User]

    def findByNameSql(name: UserName): Query0[User] =
      sql"""
           select (id, name) from users
           where name = ${name.value}
         """.query

    def findByIdSql(id: UserId): Query0[User] =
      sql"""
           select (id, name) from users
           where id = ${id.value}
         """.query

    def findWithPassSql(id: UserId): Query0[UserWithPassword] =
      sql"""
           select * from users
           where id = ${id.value}
         """.query[UserWithPassword]

    def createUserSql(createUser: CreateUser): Update0 =
      sql"""
           insert into users (name, passwd)
           values (${createUser.name.value}, ${createUser.password.value})
         """.update

    def deleteUserSql(userId: UserId): Update0 =
      sql"""
           delete from users where id = ${userId.value}
         """.update
  }
}