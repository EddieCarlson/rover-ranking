package rover

import scala.io.Source
import scala.util.Using

case class SitterReview(name: String, rating: Int, email: String)

object SitterReview {
  def parse(filepath: String): List[SitterReview] =
    Using(Source.fromFile(filepath)) { src =>
      val linesWithoutHeader = src.getLines.drop(1)
      val trimmedLineLists = linesWithoutHeader.map(_.split(",").map(_.trim).toList)
      trimmedLineLists.map(parseReview).toList
    }.get

  def parseReview(line: List[String]): SitterReview = line match {
    case List(rating, _, _, _, _, _, sitter, _, _, _, email, _, _, _) =>
      SitterReview(sitter, Integer.parseInt(rating), email)
    case _ => /* bad */ throw new Exception("bad")
  }
}
