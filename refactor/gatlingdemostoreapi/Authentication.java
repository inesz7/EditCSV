package gatlingdemostoreapi;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.LocalDateTime;

public class Authentication {
        public static ChainBuilder authenticate = doIf(session -> !session.getBoolean("authenticated")).then(
                        exec(http("Authenticate")
                                        .post("/api/authenticate")
                                        .body(StringBody("{\"username\": \"admin\",\"password\": \"admin\"}"))
                                        .check(status().is(200))
                                        .check(jmesPath("token").saveAs("jwt")))
                                        .exec(session -> session.set("authenticated", true)));
}
