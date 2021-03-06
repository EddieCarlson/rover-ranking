package rover.scoring

import rover.input.Review
import rover.scoring.SitterScore.twoDecimal

// represents a sitter and the various scores assigned to them, calculated from their aggregated reviews
case class SitterScore(name: String, email: String, profileScore: Double, ratingsScore: Double, searchScore: Double)
  extends Ordered[SitterScore] {

  val twoDecimalSearchScore: String = twoDecimal(searchScore) // stored since it is used for sorting

  // creates a comma-separated list that is the expected format in the output csv
  def format: String = s"$email,$name,${twoDecimal(profileScore)},${twoDecimal(ratingsScore)},$twoDecimalSearchScore\n"

  // sorts first by rounded search score (desc), then by name (asc)
  def compare(that: SitterScore): Int = {
    val roundedScoreComp = that.twoDecimalSearchScore.compare(twoDecimalSearchScore) // desc by scores
    if (roundedScoreComp == 0) {
      name.compare(that.name) // if rounded scores are equal, compare names
    } else {
      roundedScoreComp
    }
  }
}

object SitterScore {
  val twoDecimalFormat = "%.2f"

  def apply(name: String, email: String, ratings: List[Int]): SitterScore = { // (`apply` is the conventional name here)
    val Scores(profile, rating, search) = Scoring.scores(name, ratings)
    SitterScore(name, email, profile, rating, search)
  }

  // aggregates a list of `Review`s into a list of SitterScores (one element per distinct sitter in input list)
  def fromReviews(reviews: List[Review]): List[SitterScore] =
    reviews.groupBy(review => (review.sitterName, review.email)).map {
      case ((name, email), reviews) => SitterScore(name, email, reviews.map(_.rating))
    }.toList

  def twoDecimal(d: Double): String = twoDecimalFormat.format(d)
}
