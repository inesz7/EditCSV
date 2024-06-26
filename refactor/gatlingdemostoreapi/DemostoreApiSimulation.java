package gatlingdemostoreapi;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.LocalDateTime;

public class DemostoreApiSimulation extends Simulation {

        private HttpProtocolBuilder httpProtocol = http
                        .baseUrl("https://demostore.gatling.io")
                        .header("Cache-Control", "no-cache")
                        .contentTypeHeader("application/json")
                        .acceptHeader("application/json");

        private static ChainBuilder initSession = exec(session -> session.set("authenticated", false));

        private ScenarioBuilder scn = scenario("DemostoreApiSimulation")
                        .exec(initSession)
                        .exec(Categories.list)
                        .pause(2)
                        .exec(Products.list)
                        .pause(2)
                        .exec(Products.get)
                        .pause(2)
                        .exec(Products.update)
                        .pause(2)
                        .doIf(session -> session.getString("newProductName").equals(session.getString("productName")))
                        .then(repeat(3).on(exec(Products.create)))
                        .pause(2)
                        .exec(Categories.update)
                        .pause(2)
                        .doWhileDuring(session -> session.getInt("status") != 200, 20)
                        .on(exec(Products.checkProductId));

        {
                setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
        }
}
