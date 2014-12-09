import com.twitter.finatra._

object TrussApp extends App {
  
  println("===============================================================")
  println("|                         Truss Webapp                        |")
  println("===============================================================")
  println()

  this.args match {
    case Array("help")    => println(usageGuide)
    case Array()          => bootServer()
    case Array(port)      => bootServer(port)
    case Array(port, env) => bootServer(port, env)
  }
  
  def bootServer(port: String = "8080", env: String = "development") {
    System.setProperty("com.twitter.finatra.config.env", env)
    System.setProperty("com.twitter.finatra.config.port", s":$port")
    System.setProperty("com.twitter.finatra.config.adminPort", "")
    System.setProperty("com.twitter.finatra.config.appName", "Truss")
    println(s"Starting $env server on port $port")
    
    val server = new FinatraServer
    server.register(new TrussController)
    server.start()
  }
  
  lazy val usageGuide = fromResource("/usage.txt")
  
  lazy val sample = fromResource("/sample.txt")
  
  def fromResource(filename: String) =
    io.Source.fromInputStream(getClass.getResourceAsStream(filename)).mkString
  
}

class TrussController extends Controller {

  import com.google.gson.JsonParser
  import edu.vanderbilt.truss.struct._
  import edu.vanderbilt.truss.TrussEngine

  val parser = new JsonParser

  def renderJson = (s: String) =>
    render.status(200)
      .header("Content-Type", "application/json")
      .body(s)

  post("/api/computation") { request =>
    val jsonInput = parser.parse(request.contentString)
    val inputSet = InputSet.fromJson(jsonInput.getAsJsonObject)
    val resultSet = new TrussEngine(inputSet).compute
    
    renderJson(resultSet.writeToJson).toFuture
  }
  
  get("/api/sample") { request =>
    render.plain(TrussApp.sample).toFuture
  }
  
  get("/") { request =>
    render.static("main.html").toFuture
  }
  
  get("/test") { request =>
    render.static("test.html").toFuture
  }
  
}

