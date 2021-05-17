package rover

import scala.io.Source
import scala.util.{Try, Using}

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

import SitterReviewValidators.validateReview

// contains the relevant information to be gleaned from each row in the input dataset
case class SitterReview(name: String, rating: Int, email: String)

// Parse SitterReviews from a csv in the expected format
object SitterReviewParsers {
  // given the filepath to a csv file in the expected format, returns either a list of parsed SitterReviews,
  // or an exception with a message detailing all parsing and validation errors (possibly more than one per row),
  // including row numbers and the offending lines
  def parseFile(filepath: String): Either[Throwable, List[SitterReview]] =
    Using(Source.fromFile(filepath)) { src =>
      parseLines(src.getLines).leftMap(combineErrors).toEither
    }.toEither.joinRight

  // given an iterator where each element is a string corresponding to a line in the input csv file of the expected
  // format, parses and validates those lines into SitterReviews or a list of string error messages
  def parseLines(lines: Iterator[String]): ValidatedNel[String, List[SitterReview]] = {
    val withoutHeader = lines.drop(1) // could add validation on expected header
    val commaLists = withoutHeader.map(_.split(",", -1).map(_.trim).toList)
    val reviewValidations = commaLists.map(parseReview).toList
    addErrorRowIndices(reviewValidations).sequence
  }

  // given a list of strings where each element is a field in the comma-separated row from the input csv of the
  // expected format, validates those elements as a SitterReview or a list of error messages
  def parseReview(line: List[String]): ValidatedNel[String, SitterReview] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      val reviewValidation = validateReview(rating, sitter, email)
      addErrorLineOnce(reviewValidation, line.mkString(","))
    case _ =>
      s"line did not contain 14 elements: ${line.mkString(",")}".invalidNel
  }

  // ---- error handling helpers ----

  def combineErrors(errorMessages: NonEmptyList[String]): Throwable =
    new IllegalArgumentException(s"errors during parsing:\n${errorMessages.mkString_("\n")}")

  def addErrorRowIndices[A](list: List[ValidatedNel[String, A]]): List[ValidatedNel[String, A]] =
    list.zipWithIndex.map { case (v, idx) => // for any validations that are failures, add row number to err msg
      v.leftMap(msgs => msgs.map(msg => s"(row ${idx + 1}) $msg"))
    }

  // add offending input row to a validation error message. if a validation for a single row in the csv has multiple
  // validation errors, include the row only in the first error msg, rather than duplicating it in all msgs for the row
  def addErrorLineOnce[A](v: ValidatedNel[String, A], line: String): ValidatedNel[String, A] =
    v.leftMap { case NonEmptyList(head, tail) =>
      NonEmptyList(s"$head in line: $line", tail)
    }
}

// simple validators that take parsed strings and turn them into SitterReview (components)
object SitterReviewValidators {
  def validateReview(rating: String, sitter: String, email: String): ValidatedNel[String, SitterReview] =
    (validateSitter(sitter), validateRating(rating), validateEmail(email)).mapN(SitterReview.apply)

  def validateRating(intStr: String): ValidatedNel[String, Int] =
    Try(intStr.toInt).filter(r => r >= 0 && r <= 5).toValidated
      .leftMap(_ => s"rating '$intStr' was not an integer between 0 and 5")
      .toValidatedNel

  def validateSitter(sitter: String): ValidatedNel[String, String] =
    Validated.cond(sitter.nonEmpty, sitter, s"sitter field was not present").toValidatedNel

  def validateEmail(email: String): ValidatedNel[String, String] =
    Validated.cond(email.contains('@'), email, s"email '$email' did not contain '@'").toValidatedNel
}