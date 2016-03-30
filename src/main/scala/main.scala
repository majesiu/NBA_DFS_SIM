/**
  * Created by Majesiu on 2016-03-28.
  */
import scalaj.http.Http
import java.io._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Main extends App {

  case class Team(name: List[String])

  override def main(args: Array[String]): Unit = {

    val d2 = Http("http://stats.nba.com/stats/scoreboard/")
          .param("GameDate","03/27/2016")
          .param("LeagueID","00")
          .param("DayOffset","0")
          .header("user-agent", """Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5)
               AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36""")
          .header( "referer", "http://stats.nba.com/scores/")
          .asString.toString.split('(')

    val d = d2.drop(1).mkString("(").split('}')
    val data = d.take(d.size-1).mkString("}").concat("}")

    val js = Json.parse(data)

    val datasets = (js \ "resultSets").get
    val games =  datasets(1)


    val file = new File("C:\\Users\\Majesiu\\Desktop\\datasets.json")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(Json.prettyPrint(datasets))
    bw.close()


    val file2 = new File("C:\\Users\\Majesiu\\Desktop\\games.json")
    val bw2 = new BufferedWriter(new FileWriter(file2))
    bw2.write(Json.prettyPrint(games.get))
    bw2.close()
  }

}
