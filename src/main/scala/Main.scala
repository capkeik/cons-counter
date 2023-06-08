import cats.effect.{ExitCode, IO, IOApp}
import tofu.logging.Logging
import cats.effect.std.Console


object Main extends IOApp {
  private val mainLogs =
    Logging.Make.plain[IO].byName("Main")

  override def run(args: List[String]): IO[ExitCode] = {
    for {
      _ <- Console[IO].println("Welcome to My IO App!")
      _ <- Console[IO].println("Please enter your name:")
      name <- Console[IO].readLine
      _ <- Console[IO].println(s"Hello, $name!")
    } yield ExitCode.Success
  }

}
