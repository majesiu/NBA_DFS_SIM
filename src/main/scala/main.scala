/**
  * Created by Majesiu on 2016-03-28.
  */
import scalaj.http.Http
import java.io._
import play.api.libs.json._
import play.api.libs.functional.syntax._

object main extends App {

  case class Team(name: List[String])
  case class Avalaible(GAMEID: String, PT_AVAILABLE: Int)
  case class Datarow(name: String, headers: List[String],rowSet: List[Avalaible])

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
    val data = d.take(d.length-1).mkString("}").concat("}")

    val js = Json.parse(data)

    val datasets = (js \ "resultSets").get
    val games =  datasets(6).get
    val name = (games \ "headers").get

    println(Json.prettyPrint(name))

    val file = new File("I:\\Users\\majesiu\\Desktop\\datasets.json")
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(Json.prettyPrint(datasets))
    bw.close()

    implicit val avalaibleReads: Reads[Avalaible] = (
        JsPath(0).read[String] and
        JsPath(1).read[Int]
      )(Avalaible.apply _)

    implicit val datarowReads: Reads[Datarow] = (
        (JsPath  \ "name").read[String] and
        (JsPath  \ "headers").read[List[String]] and
        (JsPath  \ "rowSet").read[List[Avalaible]]
      )(Datarow.apply _)

    val datarowResult: JsResult[Datarow] = games.validate[Datarow]

    println(datarowResult.get)

    val file2 = new File("I:\\Users\\majesiu\\Desktop\\games.json")
    val bw2 = new BufferedWriter(new FileWriter(file2))
    bw2.write(Json.prettyPrint(games))
    bw2.close()
  }

}
