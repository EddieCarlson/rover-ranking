package rover

import scala.io.Source
import scala.util.Using

import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

case class SitterReview(name: String, rating: Int, email: String)

object SitterReview {
  def parseFile(filepath: String): Either[Throwable, List[SitterReview]] = {
    Using(Source.fromFile(filepath)) { src =>
      parseLines(src.getLines).leftMap(combineErrors).toEither
    }.toEither.joinRight
  }

  def parseLines(lines: Iterator[String]): ValidatedNel[String, List[SitterReview]] = {
    val withoutHeader = lines.drop(1)
    val commaLists = withoutHeader.map(_.split(",", -1).map(_.trim).toList)
    commaLists.map(parseReview).toList.sequence
  }

  def parseReview(line: List[String]): ValidatedNel[String, SitterReview] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      parseRating(rating, line).map(SitterReview(sitter, _, email))
    case _ =>
      s"line did not contain 14 elements: ${line.mkString(",")}".invalidNel
  }

  def parseRating(intStr: String, line: List[String]): ValidatedNel[String, Int] =
    Validated.catchNonFatal(intStr.toInt)
      .leftMap(_ => s"rating '$intStr' was not an integer in row: ${line.mkString(",")}")
      .toValidatedNel

  def combineErrors(errorMessages: NonEmptyList[String]): Throwable =
    new IllegalArgumentException(s"errors during parsing:\n${errorMessages.mkString_("\n")}")
}
