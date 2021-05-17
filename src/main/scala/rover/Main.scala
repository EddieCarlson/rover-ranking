package rover

import cats.implicits._

// reads a csv file specified via the first (and only) program argument
object Main extends App {
  val successOrError: Either[Throwable, Unit] =
    for {
      inputFileName <- ParseArgs.parse(args)
      reviews <- SitterReviewParsers.parseFile(inputFileName)
      scores = SitterScore.fromReviews(reviews)
      outputFileName <- SitterWriter.write(scores.sorted)
    } yield outputFileName

  successOrError match {
    case Right(outputFileName) => println(s"success: $outputFileName created")
    case Left(t) => println(s"failure: ${t.getMessage}") // NOTE: NOT thrown/caught, returned respecting control flow
  }
}

object ParseArgs {
  def parse(args: Array[String]): Either[Throwable, String] = args.toList match {
    case filename :: Nil => filename.asRight
    case _ =>
      val msg = s"program must be executed with exactly one arg: file path of the reviews csv. provided: ${args.toList}"
      new IllegalArgumentException(msg).asLeft
  }
}