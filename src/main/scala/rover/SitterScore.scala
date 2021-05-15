package rover

import SitterScore.twoDecimal
import Scoring.{profileScore, searchScore}

case class SitterScore(name: String, email: String, profileScore: Double, ratingsScore: Double, searchScore: Double)
  extends Ordered[SitterScore] {

  def format: String =
    s"$email,$name,${twoDecimal(profileScore)},${twoDecimal(ratingsScore)},${twoDecimal(searchScore)}\n"

  def compare(that: SitterScore): Int =
    if (searchScore == that.searchScore) {
      name.compare(that.name) // asc by name if scores are tied
    } else {
      that.searchScore.compare(searchScore) // desc by scores
    }
}

object SitterScore {
  val twoDecimalFormat = "%.2f"

  def apply(name: String, email: String, ratings: List[Int]): SitterScore = {
    val profScore = profileScore(name)
    val ratingsLength = ratings.length
    val ratingsScore = ratings.sum.toDouble / ratingsLength
    val srchScore = searchScore(profScore, ratingsScore, ratingsLength)
    SitterScore(name, email, profScore, ratingsScore, srchScore)
  }

  def fromReviews(reviews: List[SitterReview]): List[SitterScore] =
    reviews.groupBy(review => (review.name, review.email)).map {
      case ((name, email), reviews) => SitterScore(name, email, reviews.map(_.rating))
    }.toList

  def twoDecimal(d: Double): String = twoDecimalFormat.format(d)
}
