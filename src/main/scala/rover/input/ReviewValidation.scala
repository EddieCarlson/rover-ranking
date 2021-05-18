package rover.input

import scala.util.Try

import cats.data.{Validated, ValidatedNel}
import cats.implicits._

// simple validators that take extracted strings and turn them into `Review` (components) or error messages
object ReviewValidation {
  val expectedHeader: String = "rating,sitter_image,end_date,text,owner_image,dogs,sitter,owner,start_date," +
    "sitter_phone_number,sitter_email,owner_phone_number,owner_email,response_time_minutes"

  def validateReview(rating: String, sitter: String, email: String): ValidatedNel[String, Review] =
    (validateSitter(sitter), validateRating(rating), validateEmail(email)).mapN(Review.apply)

  def validateRating(intStr: String): ValidatedNel[String, Int] =
    Try(intStr.toInt).filter(r => r >= 0 && r <= 5).toOption
      .toValidNel(s"rating '$intStr' was not an integer between 0 and 5")

  def validateSitter(sitter: String): ValidatedNel[String, String] =
    Validated.cond(sitter.nonEmpty, sitter, s"sitter field was not present").toValidatedNel

  def validateEmail(email: String): ValidatedNel[String, String] =
    Validated.cond(email.contains('@'), email, s"email '$email' did not contain '@'").toValidatedNel

  // validates that the first element in the iterator matches the expected header. returns the iterator having been
  // advanced one index if the header matches, else an error message describing the failure
  def validateHeader(header: String): ValidatedNel[String, Unit] =
    if (header == expectedHeader) ().validNel
    else s"expected header: $expectedHeader\ngot: $header".invalidNel
}
