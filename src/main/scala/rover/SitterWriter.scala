package rover

import java.io.{File, PrintWriter}
import scala.util.Using

object SitterWriter {
  val outputHeader = "email,name,profile_score,ratings_score,search_score\n"
  val outputFileName = "sitters.csv"

  def write(sitterScores: List[SitterScore]): Unit =
    Using(new PrintWriter(new File(outputFileName))) { writer =>
      writer.write(outputHeader)
      sitterScores.map(_.format).foreach(writer.write)
    }
}
