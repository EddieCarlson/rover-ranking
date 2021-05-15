package rover

import SitterScore.twoDecimal

case class SitterScore(name: String, email: String, profileScore: Double, ratingsScore: Double, searchScore: Double) {
  def format: String =
    s"$email,$name,${twoDecimal(profileScore)},${twoDecimal(ratingsScore)},${twoDecimal(searchScore)}\n"
}

object SitterScore {
  val alphabetSize = 26
  val twoDecimalFormat = "%.2f"

  def apply(name: String, email: String, ratings: List[Int]): SitterScore = {
    val profScore = profileScore(name)
    val ratingsLength = ratings.length
    val ratingsScore = ratings.sum.toDouble / ratingsLength
    val srchScore = searchScore(profScore, ratingsScore, ratingsLength)
    SitterScore(name, email, profScore, ratingsScore, srchScore)
  }

  def profileScore(name: String): Double = {
    val distinctLetters = name.toLowerCase.replaceAll("[^a-z]", "").toSet.size
    5.0 * distinctLetters / alphabetSize
  }

  def searchScore(profileScore: Double, ratingsScore: Double, numRatings: Int): Double = {
    val ratingsWeight = Math.min(Math.max(numRatings / 10.0, 0), 1)
    val profileWeight = 1 - ratingsWeight
    (ratingsScore * ratingsWeight) + (profileScore * profileWeight)
  }

  def twoDecimal(d: Double): String = twoDecimalFormat.format(d)

  implicit val sitterScoreOrdering: Ordering[SitterScore] = (x: SitterScore, y: SitterScore) => {
    if (x.searchScore == y.searchScore) {
      x.name.compare(y.name) // asc by name if scores are tied
    } else {
      y.searchScore.compare(x.searchScore) // desc by scores
    }
  }
}

