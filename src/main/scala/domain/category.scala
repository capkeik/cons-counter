package domain

import cats.instances.uuid
import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import eu.timepit.refined.auto.autoUnwrap
import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import tofu.logging.derivation.show

import java.util.UUID

object category {
  @derive(decoder, encoder, eqv, show, uuid)
  @newtype case class CategoryId(value: UUID)

  @derive(decoder, encoder, eqv, show)
  @newtype case class CategoryName(value: String)

  @derive(decoder, encoder)
  case class CategoryType(value: TransactionDirection)

  @newtype
  case class CategoryNameParam(value: NonEmptyString) {
    def toDomain: CategoryName = CategoryName(value.toLowerCase.capitalize)
  }

  @newtype
  case class CategoryTypeParam(value: NonEmptyString) {
    def toDomain: CategoryType = {
      val str = value.toLowerCase
      CategoryType(
        str match {
        case Income.str => Income
        case Outcome.str => Outcome
        }
      )
    }
  }

  @derive(decoder, encoder, show)
  case class CreateCategoryParam(
    name: CategoryNameParam,
    catType: CategoryTypeParam
  ) {
    def toDomain: CreateCategory = {
      CreateCategory(
        name.toDomain,
        catType.toDomain
      )
    }
  }

  case class CreateCategory(
    name: CategoryName,
    categoryType: CategoryType
  )

  @derive(decoder, encoder, eqv, show)
  case class Category(id: CategoryId, name: CategoryName)
}
