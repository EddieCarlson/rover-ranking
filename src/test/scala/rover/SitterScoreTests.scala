package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Scoring.{profileScore, searchScore}

class SitterScoreTests extends AnyFlatSpec with Matchers {

  "fromReviews" should "work for 0 reviews" in {
    SitterScore.fromReviews(Nil) shouldEqual Nil
  }

  it should "work for 1 review" in {
    val review = SitterReview("jim", 2, "jim@email.com")
    val profScore = profileScore(review.name)
    val srchScore = searchScore(profScore, 2, 1)

    SitterScore.fromReviews(List(review)) shouldEqual List(SitterScore("jim", "jim@email.com", profScore, 2, srchScore))
  }

  it should "work for 2 reviews, different sitters" in {
    val jim = SitterReview("jim", 2, "jim@email.com")
    val jimProfScore = profileScore(jim.name)
    val jimSrchScore = searchScore(jimProfScore, 2, 1)
    val bob = SitterReview("Bobby B.", 3, "bob@email.com")
    val bobProfScore = profileScore(bob.name)
    val bobSrchScore = searchScore(bobProfScore, 3, 1)

    SitterScore.fromReviews(List(jim, bob)) should contain theSameElementsAs List(
      SitterScore("jim", "jim@email.com", jimProfScore, 2, jimSrchScore),
      SitterScore("Bobby B.", "bob@email.com", bobProfScore, 3, bobSrchScore)
    )
  }

  it should "work with repeat sitters" in {
    val jim = SitterReview("jim", 2, "jim@email.com")
    val jim2 = SitterReview("jim", 3, "jim@email.com")
    val jim3 = SitterReview("jim", 5, "jim@email.com")
    val jimProfScore = profileScore(jim.name)
    val jimRatingScore = 10.0 / 3
    val jimSrchScore = searchScore(jimProfScore, jimRatingScore, 3)

    val bob = SitterReview("Bobby B.", 3, "bob@email.com")
    val bobProfScore = profileScore(bob.name)
    val bobSrchScore = searchScore(bobProfScore, 3, 1)

    SitterScore.fromReviews(List(jim, jim2, bob, jim3)) should contain theSameElementsAs List(
      SitterScore("jim", "jim@email.com", jimProfScore, jimRatingScore, jimSrchScore),
      SitterScore("Bobby B.", "bob@email.com", bobProfScore, 3, bobSrchScore)
    )
  }

  "format" should "set to 2 decimal points" in {
    SitterScore("name", "email@e.com", 2, 3.04444444, 4.357).format shouldEqual "email@e.com,name,2.00,3.04,4.36\n"
  }

  "sorting" should "sort first by search score (desc), then by name (asc)" in {
    val scores = List(
      SitterScore("a", "aa", 1, 2.1, 3.1),
      SitterScore("c", "cc", 1.1, 2, 3.2),
      SitterScore("b", "bb", 1.1, 2, 3.2),
      SitterScore("d", "dd", 1.1, 2, 3.0),
      SitterScore("e", "dd", 1.1, 2, 3.1),
    )

    scores.sorted.map(_.name) shouldEqual List("b", "c", "a", "e", "d")
  }
}
