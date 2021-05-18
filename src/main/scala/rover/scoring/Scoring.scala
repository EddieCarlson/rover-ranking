package rover.scoring

// all the types of sitter scores
case class Scores(profileScore: Double, ratingsScore: Double, searchScore: Double)

// standalone scoring calculations for the different types of sitter scores
object Scoring {
  val alphabetSize = 26
  val profileScoreCoefficient = 5.0

  // calculate all scores based on name and ratings
  def scores(name: String, ratings: List[Int]): Scores = {
    val profile = profileScore(name)
    val rating = ratingsScore(ratings)
    val search = searchScore(profile, rating, ratings.length)
    Scores(profile, rating, search)
  }

  // the average rating
  def ratingsScore(ratings: List[Int]): Double = ratings.sum.toDouble / ratings.length

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
