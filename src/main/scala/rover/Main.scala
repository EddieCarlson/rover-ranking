package rover

object Main extends App {

  // TODO: make safe
  val filepath = args.head

  val sitterReviews = SitterReview.parse(filepath)
  val sitterScores = sitterReviews.groupBy(info => (info.name, info.email)).map {
    case ((name, email), infos) => SitterScore(name, email, infos.map(_.rating))
  }.toList

  val sortedScoreGroups = sitterScores.groupBy(_.searchScore).toList.sortBy(_._1).reverse.map(_._2)
  val sortedScores = sortedScoreGroups.flatMap(_.sortBy(_.name))

  SitterWriter.write(sortedScores)
}
