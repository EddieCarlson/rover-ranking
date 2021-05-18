package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import rover.scoring.Scoring._

class ScoringTests extends AnyFlatSpec with Matchers {
  val tolerance: Double = 0.0001 // when comparing doubles, a tolerance must be used
  val jimProfScore: Double = 5.0 * 3 / 26

  "ratings score" should "be the average (simple)" in {
    ratingsScore(List(2, 3, 4)) shouldEqual 3.0 +- tolerance
  }

  it should "be the average" in {
    ratingsScore(List(2, 2, 3)) shouldEqual 2.3333333333 +- tolerance
  }

  "profile score" should "calculate simple case" in {
    profileScore("jim") shouldEqual jimProfScore +- tolerance
  }

  it should "ignore duplicates" in {
    profileScore("jiiiimmmmmm") shouldEqual jimProfScore +- tolerance
  }

  it should "ignore capitalization" in {
    profileScore("jimM") shouldEqual jimProfScore +- tolerance
  }

  it should "ignore spaces" in {
    profileScore("   j   im       ") shouldEqual jimProfScore +- tolerance
  }

  it should "ignore other non-english-letters" in {
    profileScore("~üj]i-m.^") shouldEqual jimProfScore +- tolerance
  }

  it should "calculate provided test" in {
    profileScore("Lelani R.") shouldEqual (5.0 * 6 / 26) +- tolerance
  }

  it should "do it all" in {
    profileScore("A $really-__--   ü  bad nAMMMmmme") shouldEqual (5.0 * 9 / 26) +- tolerance
  }

  // ratings are guaranteed to be 0-5 by the validators, and numRatings is guaranteed to be > 1, so testing
  // outside these values is unnecessary
  "search score" should "calculate with 10 ratings" in {
    searchScore(2, 1, 10) shouldEqual 1.0 +- tolerance
  }

  it should "cap at 10" in {
    searchScore(2, 1, 100) shouldEqual 1.0 +- tolerance
  }

  it should "work with 1 rating" in {
    searchScore(2, 1, 1) shouldEqual ((2 * 0.9) + (1 * 0.1)) +- tolerance
  }

  it should "work with 4 ratings" in {
    searchScore(2, 1, 4) shouldEqual ((2 * 0.6) + (1 * 0.4)) +- tolerance
  }

  it should "work with 7 ratings and non-ints" in {
    searchScore(2.1, 1.1, 7) shouldEqual ((2.1 * 0.3) + (1.1 * 0.7)) +- tolerance
  }
}
