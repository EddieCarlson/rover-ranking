package rover

import cats.implicits._

object Main extends App {
  (for {
    filepath <- ParseArgs.parse(args)
    reviews <- SitterReview.parseFile(filepath)
    scores = SitterScore.fromReviews(reviews)
    _ <- SitterWriter.write(scores.sorted)
  } yield {
    println("success")
  }).recover {
    case t: Throwable => println(s"failure: $t")
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