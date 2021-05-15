package rover

import SitterScore.sitterScoreOrdering

object Main extends App {

  // TODO: make safe
  val filepath = args.head

  val sitterReviews = SitterReview.parse(filepath)
  val sitterScores = sitterReviews.groupBy(info => (info.name, info.email)).map {
    case ((name, email), infos) => SitterScore(name, email, infos.map(_.rating))
  }.toList

  SitterWriter.write(sitterScores.sorted)
}
