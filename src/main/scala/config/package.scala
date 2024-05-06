import java.nio.file.{Files, Paths}

package object config {

  def readCsv(path: String): Either[String, List[String]] = {
    try {
      val lines = Files.readAllLines(Paths.get(path))
      Right(lines.toArray(Array.empty[String]).toList)
    } catch
      case e: Exception => Left(s"Error reading csv file: ${e.getMessage}")
  }
}
