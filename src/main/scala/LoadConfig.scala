import config.Config

object LoadConfig {

  def main(args: Array[String]): Unit = {
    new Config().createConfig()
  }
}
