package config

import scala.collection.mutable

class Config {

  private val regex = ","
  private val configFilePath = "src/main/resources/config.csv"

  def createConfig(): Map[String, Map[(String, String), String]] = {
    readCsv(configFilePath) match {
      case Right(lines) =>
        val configMutable: mutable.Map[String, mutable.Map[(String, String), String]] = mutable.Map()
        var currentKey: Option[String] = None
        var sectionLines: List[String] = List.empty
        for (line <- lines) {
          if (!line.contains(regex)) {
            currentKey.foreach { key =>
              createMatrixFromLines(sectionLines).foreach { matrix =>
                val values = findPairsAndValue(matrix)
                configMutable.getOrElseUpdate(key, mutable.Map.empty[(String, String), String]) ++= values
              }
            }
            currentKey = Some(line.trim)
            sectionLines = List.empty
          } else {
            sectionLines :+= line
          }

        }
        currentKey.foreach { key =>
          createMatrixFromLines(sectionLines).foreach { matrix =>
            val values = findPairsAndValue(matrix)
            configMutable.getOrElseUpdate(key, mutable.Map.empty[(String, String), String]) ++= values
          }
        }
        convertMutableMapToImmutable(configMutable)
      case Left(_) => ???
    }
  }

  private[config] def createMatrixFromLines(lines: List[String]): Option[List[List[String]]] = {
    if (lines.isEmpty) {
      None
    } else {
      Some(lines.filter(_.contains(regex)).map(_.split(regex).map(_.trim).toList))
    }
  }

  private[config] def findPairsAndValue(matrix: List[List[String]]): List[((String, String), String)] = {
    val firstLabels = matrix.tail.map(_.head)
    val secondLabels = matrix.head.tail
    val values = matrix.tail.map(_.tail)

    val pairValues = for {
      i <- firstLabels.indices
      j <- secondLabels.indices
    } yield {
      ((firstLabels(i), secondLabels(j)), values(i)(j))
    }
    pairValues.toList
  }

  private def convertMutableMapToImmutable[K, V](mutableMap: mutable.Map[K, mutable.Map[V, String]]): Map[K, Map[V, String]] = {
    mutableMap.map {
      case (key, innerMap) => key -> innerMap.toMap
    }.toMap
  }
}

