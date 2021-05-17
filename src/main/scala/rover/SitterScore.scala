package rover

import Scoring.{profileScore, searchScore}
import SitterScore.twoDecimal

// represents a sitter and the various scores assigned to them from having processed all their reviews
case class SitterScore(name: String, email: String, profileScore: Double, ratingsScore: Double, searchScore: Double)
  extends Ordered[SitterScore] {

  val twoDecimalSearchScore: String = twoDecimal(searchScore) // stored since it is used for sorting

  // creates a comma-seperated list that is the expected format in the output csv
  def format: String =
    s"$email,$name,${twoDecimal(profileScore)},${twoDecimal(ratingsScore)},$twoDecimalSearchScore\n"

  def compare(that: SitterScore): Int =
    if (searchScore == that.searchScore) {
      name.compare(that.name) // asc by name if scores are tied
    } else {
      that.twoDecimalSearchScore.compare(twoDecimalSearchScore) // desc by scores (rounding BEFORE sort)
    }
}

object SitterScore {
  val twoDecimalFormat = "%.2f"

  // (secondary constructor - `apply` is the conventional name in the companion object)
  def apply(name: String, email: String, ratings: List[Int]): SitterScore = {
    val profScore = profileScore(name)
    val ratingsLength = ratings.length // guaranteed non-empty
    val ratingsScore = ratings.sum.toDouble / ratingsLength
    val srchScore = searchScore(profScore, ratingsScore, ratingsLength)
    SitterScore(name, email, profScore, ratingsScore, srchScore)
  }

  // processes and aggregates a list of SitterReviews into a list of SitterScores (one element per distinct sitter)
  def fromReviews(reviews: List[SitterReview]): List[SitterScore] =
    reviews.groupBy(review => (review.name, review.email)).map {
      case ((name, email), reviews) => SitterScore(name, email, reviews.map(_.rating))
    }.toList

  def twoDecimal(d: Double): String = twoDecimalFormat.format(d)
}
