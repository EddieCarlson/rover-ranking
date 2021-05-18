package rover

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import SitterReviewParsers.{parseLines, parseReview}
import cats.data.NonEmptyList

class ParsingTests extends AnyFlatSpec with Matchers {

  "parseReview" should "extract correct fields" in {
    val reviewLine = List("5", "https://images.dog.ceo/breeds/affenpinscher/n02110627_10437.jpg", "2013-04-17", "Augue donec...ok cool", "https://images.dog.ceo/breeds/puggle/IMG_095543.jpg", "Bandit|Duke|Pogo", "Joei B.", "Melinda G.", "2013-04-13", "+17953174861", "user1943@verizon.net", "+13494618669", "user4832@t-mobile.com", "110")
    parseReview(reviewLine).toOption.get shouldEqual SitterReview("Joei B.", 5, "user1943@verizon.net")
  }

  "parseLines" should "remove csv header and construct Reviews with correct info" in {
    val lines = Iterator(
      "rating,sitter_image,end_date,text,owner_image,dogs,sitter,owner,start_date,sitter_phone_number,sitter_email,owner_phone_number,owner_email,response_time_minutes",
      "5,https://images.dog.ceo/breeds/dalmatian/cooper2.jpg,2013-04-08,latin_latin_latin,https://images.dog.ceo/breeds/hound-ibizan/n02091244_327.jpg,Pinot Grigio,Lauren B.,Shelli K.,2013-02-26,+12546478758,user4739@gmail.com,+15817557107,user2555@verizon.net,2",
      "3,https://images.dog.ceo/breeds/entlebucher/n02108000_2635.jpg,2012-05-17,even_more_latin,https://images.dog.ceo/breeds/shihtzu/n02086240_1215.jpg,Rasty-CAT|Shogun|Annie,Leilani R.,Nancy L.,2012-04-03,+13813114382,user7508@t-mobile.com,+11826582052,user3444@t-mobile.com,186"
    )

    parseLines(lines).toOption.get should contain theSameElementsAs List(
      SitterReview("Lauren B.", 5, "user4739@gmail.com"),
      SitterReview("Leilani R.", 3, "user7508@t-mobile.com")
    )
  }

  // this is a very brittle test (tests exact text in error messages, which are prone to change), but I wanted to
  // demonstrate my parser's ability to generate and collect multiple, descriptive error messages at once
  it should "accumulate all errors and report them nicely" in {
    val lines = List(
      "rating,sitter_image,end_date,text,owner_image,dogs,sitter,owner,start_date,sitter_phone_number,sitter_email,owner_phone_number,owner_email,response_time_minutes",
      "5,https://images.dog.ceo/breeds/dalmatian/cooper2.jpg,2013-04-08,latin!,https://images.dog.ceo/breeds/hound-ibizan/n02091244_327.jpg,Pinot Grigio,Lauren B.,Shelli K.,2013-02-26,+12546478758,user4739@gmail.com,+15817557107,user2555@verizon.net,2",
      "oh hey there",
      "3.1,img,date,text,img,dogs,Leilani R.,Nancy L.,date,phone,user7508@t-mobile.com,phone,user3444@t-mobile.com,186",
      "25,img,date,text,img,dogs,,Nancy L.,date,phone,not_an_email,phone,user3444@t-mobile.com,186"
    )

    parseLines(lines.iterator).toEither match {
      case Left(errorMessages) =>
        errorMessages.toList shouldEqual List(
          s"(row 2) line did not contain 14 elements: ${lines(2)}",
          s"(row 3) rating '3.1' was not an integer between 0 and 5 in line: ${lines(3)}",
          s"(row 4) sitter field was not present in line: ${lines(4)}",
          s"(row 4) rating '25' was not an integer between 0 and 5",
          s"(row 4) email 'not_an_email' did not contain '@'" // entire line not repeated for duplicate rows - cleaner
        )
      case Right(reviews) =>
        fail(s"result should have been invalid. instead: $reviews")
    }
  }

  it should "complain about an empty file if there are no lines in the file" in {
    parseLines(Iterator()).toEither match {
      case Left(NonEmptyList(errorMessage, Nil)) => errorMessage should include("empty") // file was empty
      case resp => fail(s"result should have been invalid with one error message. instead: $resp")
    }
  }

  it should "complain if the number of columns is wrong, without producing further error messages" in {
    val lines = Iterator(
      "oh hey there,i'm not a header",
      "oh hey again,i'm not a valid row"
    )

    parseLines(lines).toEither match { // there is exactly one error message containing the expected header
      case Left(NonEmptyList(errorMessage, Nil)) => errorMessage should include(SitterReviewValidators.expectedHeader)
      case resp => fail(s"result should have been invalid with one error message. instead: $resp")
    }
  }
}