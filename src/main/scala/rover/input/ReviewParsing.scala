package rover.input

import scala.io.Source
import scala.util.Using

import cats.data.ValidatedNel
import cats.implicits._

import rover.input.ErrorMessaging._
import rover.input.ReviewValidation.{validateHeader, validateReview}

// contains the relevant information to be gleaned from each row in the input dataset
case class Review(sitterName: String, rating: Int, email: String)

// parse `Review`s from a csv in the expected format. only stores the necessary columns in memory as `Review`s.
// Note: No exceptions are thrown here. If errors occur, they are returned as `Left` or `Invalid`
object ReviewParsing {
  // given the filepath to a csv file in the expected format, returns either a list of parsed `Review`s,
  // or an exception with a message detailing all parsing and validation errors (possibly more than one per row),
  // including row numbers and the offending lines (limited to 50 error messages at most)
  def parseCsv(filepath: String): Either[Throwable, List[Review]] =
    Using(Source.fromFile(filepath)) { src =>
      parseLines(src.getLines()).leftMap(combineErrors).toEither
    }.toEither.joinRight

  // given an iterator where each element is a string corresponding to a line in the input csv file of the expected
  // format (including header), parses and validates those lines into `Review`s or a list of string error messages
  def parseLines(lines: Iterator[String]): ValidatedNel[String, List[Review]] =
    validateHeader(lines).andThen { rows =>
      val rowFieldLists = rows.map(_.split(",", -1).map(_.trim).toList)
      val reviewValidations = rowFieldLists.map(parseReview).toList
      addErrorRowIndices(reviewValidations).sequence
    }

  // given a list of strings where each element is a field in a comma-separated row from the input csv of the
  // expected format, validates those elements into a `Review` or a list of string error messages
  def parseReview(line: List[String]): ValidatedNel[String, Review] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) => // bind relevant fields, ignore the rest
      val reviewValidation = validateReview(rating, sitter, email)
      addErrorLineOnce(reviewValidation, line.mkString(","))
    case _ =>
      s"line did not contain 14 elements: ${line.mkString(",")}".invalidNel
  }
}
