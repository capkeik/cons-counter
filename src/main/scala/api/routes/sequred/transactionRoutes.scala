package api.routes.sequred


import config.RequestContext
import domain.errors.AppError
import domain.transaction.{CreateTransaction, Transaction, TransactionId}
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe._

object transactionRoutes {
  val listTransactions: PublicEndpoint[RequestContext, AppError, List[Transaction], Any] =
    endpoint.get
      .in("transactions")
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[AppError])
      .out(jsonBody[List[Transaction]])

  val findTransactionById: PublicEndpoint[(TransactionId, RequestContext), AppError, Option[Transaction], Any] =
    endpoint.get
      .in("transactions" / path[TransactionId])
      .in(header[RequestContext]("X-Request-Id"))
    .errorOut(jsonBody[AppError])
    .out(jsonBody[Option[Transaction]])

  val removeTransaction
  : PublicEndpoint[(TransactionId, RequestContext), AppError, Unit, Any] =
    endpoint.delete
      .in("todo" / path[TransactionId])
      .in(header[RequestContext]("X-Request-Id"))
      .errorOut(jsonBody[AppError])

  val createTransactiion
  : PublicEndpoint[(CreateTransaction, RequestContext), AppError, Transaction, Any] =
    endpoint.post
      .in("todo" / "create" )
      .in(header[RequestContext]("X-Request-Id"))
      .in(jsonBody[CreateTransaction])
      .errorOut(jsonBody[AppError])
      .out(jsonBody[Transaction])
}
