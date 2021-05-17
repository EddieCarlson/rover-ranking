package rover

import cats.implicits._

// reads a csv file specified via the first (and only) program argument containing sitter review data in the expected
// format (as shown in `reviews.csv`). Aggregates reviews by sitter and writes `sitters.csv` with rows representing
// each sitter and their calculated scores, ranked first by search score (desc), then by name (asc)
object Main extends App {
  val successOrError: Either[Throwable, String] =
    for {
      inputFileName <- ParseArgs.parse(args)
      reviews <- SitterReviewParsers.parseCsv(inputFileName)
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
    case filename :: Nil => filename.asRight // ensures there is exactly one program arg
    case _ =>
      val msg = s"program must be executed with exactly one arg: file path of the reviews csv. provided: ${args.toList}"
      new IllegalArgumentException(msg).asLeft
  }
}