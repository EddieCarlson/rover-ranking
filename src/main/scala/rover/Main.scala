package rover

import scala.util.{Failure, Success, Try}

object Main extends App {
  (for {
    filepath <- ParseArgs.parse(args)
    reviews <- SitterReview.parse(filepath)
    scores = SitterScore.fromReviews(reviews)
    _ <- SitterWriter.write(scores.sorted)
  } yield ()).recover {
    case t: Throwable => println(t)
  }
}

object ParseArgs {
  def parse(args: Array[String]) : Try[String] = args.toList match {
    case filename :: Nil => Success(filename)
    case _ =>
      val msg = s"program must be executed with exactly one arg: file path of the reviews csv. provided: ${args.toList}"
      Failure(new IllegalArgumentException(msg))
  }
}