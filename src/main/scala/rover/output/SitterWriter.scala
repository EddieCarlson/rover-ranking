package rover.output

import java.io.{File, PrintWriter}
import scala.util.Using

import cats.implicits._

import rover.scoring.SitterScore

// writes a list of `SitterScore`s to an output file (sitters.csv, here) in the expected format
object SitterWriter {
  val outputHeader = "email,name,profile_score,ratings_score,search_score\n"
  val outputFileName = "sitters.csv"

  def write(sitterScores: List[SitterScore]): Either[Throwable, String] =
    Using(new PrintWriter(new File(outputFileName))) { writer =>
      writer.write(outputHeader)
      sitterScores.map(_.format).foreach(writer.write)
    }.toEither.as(outputFileName)
}
