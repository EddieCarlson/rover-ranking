package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import Scoring._

class ScoringTests extends AnyFlatSpec with Matchers {

  "profile score" should "calculate simple case" in { profileScore("jim") shouldEqual 5.0 * 3 / 26 +- 0.0001 }

  it should "ignore duplicates" in { profileScore("jiiiimmmmmm") shouldEqual 5.0 * 3 / 26 +- 0.0001 }

  it should "ignore capitalization" in { profileScore("jimM") shouldEqual 5.0 * 3 / 26 +- 0.0001 }

  it should "ignore spaces" in { profileScore("   j   im       ") shouldEqual 5.0 * 3 / 26 +- 0.0001 }

  it should "ignore other non-letters" in { profileScore("  ~ j ]  i-m.    ^   ") shouldEqual 5.0 * 3 / 26 +- 0.0001 }

  it should "calculate provided test" in { profileScore("Lelani R.") shouldEqual 5.0 * 6 / 26 +- 0.0001 }

  it should "do it all" in { profileScore("A $really---___--    bad nAMMMmmme") shouldEqual 5.0 * 9 / 26 +- 0.0001 }

  "search score" should "calculate with 10 ratings" in {
    searchScore(2, 1, 10) shouldEqual 1.0 +- 0.0001
  }

  "search score" should "cap at 10" in {
    searchScore(2, 1, 100) shouldEqual 1.0 +- 0.0001
  }

  "search score" should "work with 1 rating" in {
    searchScore(2, 1, 1) shouldEqual ((2 * 0.9) + (1 * 0.1)) +- 0.0001
  }

  "search score" should "work with 4 ratings" in {
    searchScore(2, 1, 4) shouldEqual ((2 * 0.6) + (1 * 0.4)) +- 0.0001
  }

  "search score" should "work with 7 ratings and non-ints" in {
    searchScore(2.1, 1.1, 7) shouldEqual ((2.1 * 0.3) + (1.1 * 0.7)) +- 0.0001
  }
}
