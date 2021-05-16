package rover

import scala.io.Source
import scala.util.Using

case class SitterReview(name: String, rating: Int, email: String)

object SitterReview {
  def parse(filepath: String): List[SitterReview] =
    Using(Source.fromFile(filepath)) { src => parseLines(src.getLines) }.get

  def parseLines(lines: Iterator[String]): List[SitterReview] = {
    val withoutHeader = lines.drop(1)
    val commaLists = withoutHeader.map(_.split(",").map(_.trim).toList)
    commaLists.map(parseReview).toList
  }

  def parseReview(line: List[String]): SitterReview = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      SitterReview(sitter, Integer.parseInt(rating), email)
    case _ => /* bad */ throw new Exception("bad")
  }
}
