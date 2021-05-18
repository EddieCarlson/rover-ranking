package rover

import cats.implicits._

import rover.input.ReviewParsing
import rover.output.SitterWriter
import rover.scoring.SitterScore

// reads a csv file (specified by the first and only program argument) containing sitter review data in the expected
// format (as shown in `reviews.csv`). Aggregates reviews by sitter and writes `sitters.csv` with rows representing
// each sitter and their calculated scores, ranked first by search score (desc), then by name (asc)
object Main extends App {
  val outputFileOrError: Either[Throwable, String] =
    for {
      inputFileName <- ParseArgs.parse(args)
      reviews <- ReviewParsing.parseCsv(inputFileName)
      scores = SitterScore.fromReviews(reviews)
      outputFileName <- SitterWriter.write(scores.sorted)
    } yield outputFileName

  outputFileOrError match {
    case Right(outputFileName) => println(s"success: $outputFileName created")
    case Left(t) => println(s"failure: ${t.getMessage}") // not thrown/caught, returned respecting control flow
  }
}

object ParseArgs {
  def parse(args: Array[String]): Either[Throwable, String] = args.toList match {
    case filename :: Nil => filename.asRight // ensures there is exactly one program arg
    case _ =>
      val msg = s"program must be executed with exactly one arg: file path of the reviews csv. provided: ${args.toList}"
      new IllegalArgumentException(msg).asLeft
  }
}
