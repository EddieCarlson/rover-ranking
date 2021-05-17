package rover

import scala.io.Source
import scala.util.{Try, Using}

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

import SitterReviewValidators.validateReview

// contains the relevant information to be gleaned from each row in the input dataset
case class SitterReview(name: String, rating: Int, email: String)

// parse SitterReviews from a csv in the expected format. only stores the necessary columns in memory
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
    removeHeader(lines).andThen { rows =>
      val rowFieldLists = rows.map(_.split(",", -1).map(_.trim).toList)
      val reviewValidations = rowFieldLists.map(parseReview).toList
      addErrorRowIndices(reviewValidations).sequence
    }
  }

  // parses fields from the header and validates that it has 14 columns (though does not inspect column names). returns
  // the iterator having been advanced one index if the header has 14 columns, else error message describing the failure
  def removeHeader(lines: Iterator[String]): ValidatedNel[String, Iterator[String]] = {
    val splitHeader = lines.nextOption().map(_.split(","))
    splitHeader.toValidNel("specified file was empty").andThen {
      case l if l.length == 14 => lines.validNel
      case l => s"expected 14 column names in the header, got ${l.length} in $l".invalidNel
    }
  }

  // given a list of strings where each element is a field in the comma-separated row from the input csv of the
  // expected format, validates those elements as a SitterReview or a list of error messages
  def parseReview(line: List[String]): ValidatedNel[String, SitterReview] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) => // bind relevant fields, ignore the rest
      val reviewValidation = validateReview(rating, sitter, email)
      addErrorLineOnce(reviewValidation, line.mkString(","))
    case _ =>
      s"line did not contain 14 elements: ${line.mkString(",")}".invalidNel
  }

  // ---- error message embellishment helpers ----

  // gathers all error messages from parsing/validating and generates a Throwable with a message containing all error
  // messages separated by newlines (truncated at 50 so as to not be obnoxious)
  def combineErrors(errorMessages: NonEmptyList[String]): Throwable = {
    val numErrors = errorMessages.length
    val truncatedMsg =
      if (numErrors > 50) s"\n...truncated at 50. contained $numErrors errors"
      else ""
    val allErrorsMsg = errorMessages.toList.take(50).mkString("\n")
    new IllegalArgumentException(s"errors during parsing:\n$allErrorsMsg$truncatedMsg")
  }

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