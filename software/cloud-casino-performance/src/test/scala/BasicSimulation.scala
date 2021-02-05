import io.gatling.core.scenario.Simulation

import io.gatling.core.Predef._
import io.gatling.core.body.Body
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._

class BasicSimulation extends Simulation {

  val theHttpProtocolBuilder: HttpProtocolBuilder = http
    .baseUrl("https://zd03jipzcg.execute-api.eu-west-1.amazonaws.com")

  val theCommonHeaders = Map("Content-Type" -> "application/json")

  val theBody : Body = StringBody("""{"stakeAmount": 50, "betNumber": 7, "playerId": 123}""")

  val theScenarioBuilder: ScenarioBuilder = scenario("Scenario1")
    .exec(
      http("Place a single bet")
        .post("/roulette")
        .body(theBody)
        .headers(theCommonHeaders)
    )

  setUp(
    /*
     * Increase the number of users that sends requests in the scenario Scenario1 to
     * ten users during a period of 20 seconds.
     */
    theScenarioBuilder.inject(rampUsers(1000).during(60 seconds))
  ).protocols(theHttpProtocolBuilder)
}