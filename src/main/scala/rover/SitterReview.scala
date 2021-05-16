package rover

import scala.io.Source
import scala.util.{Failure, Success, Try, Using}

case class SitterReview(name: String, rating: Int, email: String)

object SitterReview {
  def parse(filepath: String): Try[List[SitterReview]] =
    Using(Source.fromFile(filepath)) { src => parseLines(src.getLines) }.flatten

  def parseLines(lines: Iterator[String]): Try[List[SitterReview]] = {
    val withoutHeader = lines.drop(1)
    val commaLists = withoutHeader.map(_.split(",").map(_.trim).toList)
    commaLists.map(parseReview).toList.foldLeft(Try(List.empty[SitterReview])) {
      case (acc, reviewTry) => acc.flatMap(reviews => reviewTry.map(_ :: reviews))
    } // the fold could be accomplished with just a `.sequence` from the Cats library, but didn't want to add dependency
  }

  def parseReview(line: List[String]): Try[SitterReview] = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      Try(Integer.parseInt(rating)).map { ratingInt =>
        SitterReview(sitter, ratingInt, email)
      }.recoverWith { _ =>
        Failure(new IllegalArgumentException(s"rating was not an integer in row: $line"))
      }
      Success(SitterReview(sitter, Integer.parseInt(rating), email))
    case _ =>
      Failure(new IllegalArgumentException(s"unexpected number of columns in row: $line"))
  }
}
