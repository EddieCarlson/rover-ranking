package rover

// standalone scoring calculations for profile and search score (ratings score excluded because it's simple)
object Scoring {
  val alphabetSize = 26
  val profileScoreCoefficient = 5.0

  // the fraction of distinct letters in the english alphabet (case-insensitive) that comprise the name
  def profileScore(name: String): Double = {
    val distinctLetters = name.toLowerCase.replaceAll("[^a-z]", "").toSet.size
    profileScoreCoefficient * distinctLetters / alphabetSize
  }

  // the weighted average of profileScore and ratingsScore, 100% profileScore @ 0 ratings, 100% ratingsScore at 10+
  def searchScore(profileScore: Double, ratingsScore: Double, numRatings: Int): Double = {
    val ratingsWeight = Math.min(Math.max(numRatings / 10.0, 0), 1)
    val profileWeight = 1 - ratingsWeight
    (ratingsScore * ratingsWeight) + (profileScore * profileWeight)
  }
}
