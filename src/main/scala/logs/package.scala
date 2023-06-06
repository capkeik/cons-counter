import config.types.IOWithContext
import tofu.logging.Logging

package object logs {
  def surroundWithLogs[Error, Res](
    inputLog: String
  )(errorOutputLog: Error => (String, Option[Throwable]))(
    successOutputLog: Res => String
  )(
    io: IOWithContext[Either[Error, Res]]
  )(
    implicit logging: Logging[IOWithContext]
  ): IOWithContext[Either[Error, Res]] =
    for {
      _ <- logging.info(inputLog)
      res <- io
      _ <- res match {
        case Left(error) => {
          val (msg, cause) = errorOutputLog(error)
          cause.fold(logging.error(msg))(cause => logging.error(msg, cause))
        }
        case Right(result) => logging.info(successOutputLog(result))
      }
    } yield res
}
