package rover

object Main extends App {

  // TODO: make safe
  val filepath = args.head

  val sitterReviews = SitterReview.parse(filepath)
  val sitterScores = SitterScore.fromReviews(sitterReviews)

  SitterWriter.write(sitterScores.sorted)
}
