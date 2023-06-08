package domain

import derevo.cats.eqv
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.Read
import io.estatico.newtype.macros.newtype
import sttp.tapir.Schema
import tofu.logging.derivation.show


object category {
  @derive(decoder, encoder)
  @newtype case class CategoryId(value: Long)
  object CategoryId {
    implicit val doobieRead: Read[CategoryId] = Read[Long].map(CategoryId(_))
    implicit val schema: Schema[CategoryId] =
      Schema.schemaForLong.map(l => Some(CategoryId(l)))(_.value)
  }

  @derive(decoder, encoder, eqv, show)
  @newtype case class CategoryName(value: String)
  object CategoryName {
    implicit val doobieRead: Read[CategoryName] = Read[String].map(CategoryName(_))
  }
  @derive(decoder, encoder)
  case class CategoryType(value: TransactionDirection)


  case class CreateCategory(
    name: CategoryName,
    categoryType: CategoryType
  )

  @derive(decoder, encoder)
  case class Category(id: CategoryId, name: CategoryName, categoryType: CategoryType)
  object Category {
    implicit val doobieRead: Read[Category] =
      Read[(Long, Long, String, String)].map
      {
        case (id, _, name, catType) if catType == "income" => Category(CategoryId(id), CategoryName(name), CategoryType(Income))
        case (id, _, name, catType) if catType == "outcome" => Category(CategoryId(id), CategoryName(name), CategoryType(Outcome))
      }
  }
}
