package rover

import java.io.{File, PrintWriter}

object SitterWriter {
  val outputHeader = "email,name,profile_score,ratings_score,search_score\n"
  val outputFileName = "sitters.csv"

  def write(sitterScores: List[SitterScore]): Unit = {
    val writer = new PrintWriter(new File(outputFileName))

    writer.write(outputHeader)
    sitterScores.map(_.format).foreach(writer.write)

    writer.close()
  }
}
