package gatlingdemostoreapi;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.LocalDateTime;

public class Headers {

    public static Map<CharSequence, String> authorizationHeaders = Map.ofEntries(
            Map.entry("authorization", "Bearer #{jwt}"));

}
