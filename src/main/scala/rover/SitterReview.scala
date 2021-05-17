package rover

import scala.io.Source
import scala.util.Using

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

case class SitterReview(name: String, rating: Int, email: String)

object SitterReview {
  def parseFile(filepath: String): Either[Throwable, List[SitterReview]] =
    Using(Source.fromFile(filepath)) { src =>
      parseLines(src.getLines).leftMap(combineErrors).toEither
    }.toEither.joinRight

  def parseLines(lines: Iterator[String]): ValidatedNel[String, List[SitterReview]] = {
    val withoutHeader = lines.drop(1) // could add validation on expected header
    val commaLists = withoutHeader.map(_.split(",", -1).map(_.trim).toList)
    commaLists.map(parseReview).zipWithIndex.map { case (review, idx) =>
      review.leftMap(msgs => msgs.map(msg => s"(row ${idx + 1}) $msg")) // add row numbers to error messages
    }.toList.sequence
  }

  def parseReview(line: List[String]): ValidatedNel[String, SitterReview] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      validateReview(rating, sitter, email).leftMap { case NonEmptyList(head, tail) =>
        NonEmptyList(s"$head in line: ${line.mkString(",")}", tail)
      } // add line to first error message for this row only, rather than repeating it for each error for the row
    case _ =>
      s"line did not contain 14 elements: ${line.mkString(",")}".invalidNel
  }

  def validateReview(rating: String, sitter: String, email: String): ValidatedNel[String, SitterReview] =
    (validateSitter(sitter), validateRating(rating), validateEmail(email)).mapN(SitterReview.apply)

  def validateRating(intStr: String): ValidatedNel[String, Int] =
    Validated.catchNonFatal(intStr.toInt).leftMap(_ => s"rating '$intStr' was not an integer").toValidatedNel

  def validateSitter(sitter: String): ValidatedNel[String, String] =
    Validated.cond(sitter.nonEmpty, sitter, s"sitter field was not present").toValidatedNel

  def validateEmail(email: String): ValidatedNel[String, String] =
    Validated.cond(email.contains('@'), email, s"email '$email' did not contain '@'").toValidatedNel

  def combineErrors(errorMessages: NonEmptyList[String]): Throwable =
    new IllegalArgumentException(s"errors during parsing:\n${errorMessages.mkString_("\n")}")
}
