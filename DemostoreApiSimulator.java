package gatlingdemostoreapi;

import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class DemostoreApiSimulation extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
          .baseUrl("https://demostore.gatling.io")
          .header("Cache-Control", "no-cache")
          .contentTypeHeader("application/json")
          .acceptHeader("application/json");


  private static Map<CharSequence, String> authorizationHeaders = Map.ofEntries(
          Map.entry("authorization", "Bearer #{jwt}")
  );

  private static ChainBuilder initSession = exec(session -> session.set("authenticated", false));

  private static class Authentication {
    private static ChainBuilder authenticate =
            doIf(session -> !session.getBoolean("authenticated")).then(
                    exec(http("Authenticate")
                            .post("/api/authenticate")
                            .body(StringBody("{\"username\": \"admin\",\"password\": \"admin\"}"))
                            .check(status().is(200))
                            .check(jmesPath("token").saveAs("jwt")))
                            .exec(session -> session.set("authenticated", true)));
  }

  private static class Categories {

    private static FeederBuilder.Batchable<String> categoriesFeeder =
            csv("data/categories.csv").random();

    private static ChainBuilder list =
            exec(http("List categories")
                    .get("/api/category")
                    .check(jmesPath("[? id == `6`].name").ofList().is(List.of("For Her"))));

    private static ChainBuilder update =
            feed(categoriesFeeder)
                    .exec(Authentication.authenticate)
                    .exec(http("Update category")
                            .put("/api/category/#{categoryId}")
                            .headers(authorizationHeaders)
                            .body(StringBody("{\"name\": \"#{categoryName}\"}"))
                            .check(jmesPath("name").isEL("#{categoryName}")));
  }

  private static class Products {

    private static FeederBuilder.Batchable<String> productsFeeder =
            csv("data/products.csv").circular();

        private static ChainBuilder list =
            exec(http("List products")
                    .get("/api/product")
                    .check(jmesPath("[*].id").ofList().saveAs("allProductIds")));

    private static ChainBuilder get =
        exec( session -> {
                List<Integer> allProductIds = session.getList("allProductIds");
                return session.set("productId", allProductIds.get(new Random().nextInt(allProductIds.size())));
        })
        .exec(
                session -> {
                        System.out.println("allProductIds captured:" + session.get("allProductIds").toString());
                        System.out.println("ProductIds selected:" + session.get("productId").toString());
                        return session;
                }
            )
        .exec(http("Get product")
                            .get("/api/product")
                            .check(jmesPath("[? id == `#{productId}`].categoryId | [0]").find().saveAs("productCategoryId"))
                            .check(jmesPath("[? id == `#{productId}`].name | [0]").find().saveAs("productName"))
                            .check(jmesPath("[? id == `#{productId}`].image | [0]").find().saveAs("productImage"))
                            .check(jmesPath("[? id == `#{productId}`].description | [0]").find().saveAs("productDescription"))
                            .check(jmesPath("[? id == `#{productId}`].price | [0]").find().saveAs("productPrice")));

    private static ChainBuilder update =
                exec(Authentication.authenticate)
              .exec(
                session -> {
                        System.out.println("Product captured:"
                        + "\n" + "Product category: " + session.get("productCategoryId") .toString()
                        + "\n" + "Product name: " + session.get("productName").toString()
                        + "\n" + "Product description: " + session.get("productDescription").toString()
                        + "\n" + "Product image: " + session.get("productImage").toString()
                        + "\n" + "Product price: " + session.get("productPrice").toString()
                        + "\n" + "Product id: " + session.get("productId").toString());
                        return session;
                })
                .exec(
                        session -> {
                                String newName;
                                do{
                                        int alea = new Random().nextInt(10);
                                        newName = "test" + alea;
                                }while(newName.equals(session.get("productName").toString()));

                                int nombreAlea = new Random().nextInt(10);

                                List<String> listBool = Arrays.asList("true", "false");
                                String bool = listBool.get(new Random().nextInt(listBool.size()));

                                System.out.println("Donnees modifees :"
                                + "\n" + "New Name : " + newName.toString()
                                + "\n" + "Nombre aleatoire : " + nombreAlea
                                + "\n" + "boolean : " + bool.toString());

                                return session
                                        .set("newProductName", newName)
                                        .set("newProductCategory", nombreAlea)
                                        .set("newBool", bool);
                        }
                )       
                .doIf(session -> session.getBoolean("newBool")).then(
                        exec(session -> {
                                System.out.println("Condition verifiee!!");
                                return session;
                        })
                                
                )
                .exec(http("Update product #{productName}")
                            .put("/api/product/#{productId}")
                            .headers(authorizationHeaders)
                            .body(ElFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json")));

    private static ChainBuilder create =
            exec(Authentication.authenticate)
                    .feed(productsFeeder)
                    .exec(http("Create product #{productName}")
                            .post("/api/product")
                            .headers(authorizationHeaders)
                            .body(ElFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json")));
  }

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
          .repeat(3).on(exec(Products.create))
          .pause(2)
          .exec(Categories.update);

  {
    setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
