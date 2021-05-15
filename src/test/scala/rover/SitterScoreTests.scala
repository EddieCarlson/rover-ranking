package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Scoring.{profileScore, searchScore}

class SitterScoreTests extends AnyFlatSpec with Matchers {

  "fromReviews" should "work for 0 reviews" in {
    SitterScore.fromReviews(Nil) shouldEqual Nil
  }

  "fromReviews" should "work for 1 review" in {
    val review = SitterReview("jim", 2, "jim@email.com")
    val profScore = profileScore(review.name)
    val srchScore = searchScore(profScore, 2, 1)
    SitterScore.fromReviews(List(review)) shouldEqual List(SitterScore("jim", "jim@email.com", profScore, 2, srchScore))
  }

  "fromReviews" should "work for 2 reviews, different sitters" in {
    val reviews = SitterReview("jim", 2, "jim@email.com")
    val profScore = profileScore(review.name)
    val srchScore = searchScore(profScore, 2, 1)
    SitterScore.fromReviews(List(review)) shouldEqual List(SitterScore("jim", "jim@email.com", profScore, 2, srchScore))
  }

}
