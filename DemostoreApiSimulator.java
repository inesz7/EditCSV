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

  //Partie interressante
  private static class Products {

    private static FeederBuilder.Batchable<String> productsFeeder =
            csv("data/products.csv").circular();

        private static ChainBuilder list =
            exec(http("List products")
                    .get("/api/product")
                    //Récupération des id de tout les produits
                    .check(jmesPath("[*].id").ofList().saveAs("allProductIds")));

    private static ChainBuilder get =
        //Définition d'un id de produit au hasard en tant que productId
        exec( session -> {
                List<Integer> allProductIds = session.getList("allProductIds");
                return session.set("productId", allProductIds.get(new Random().nextInt(allProductIds.size())));
        })
        //Affichage de la liste des id et de l'id aléatoire sélectionné
        .exec(
                session -> {
                        System.out.println("allProductIds captured:" + session.get("allProductIds").toString());
                        System.out.println("ProductIds selected:" + session.get("productId").toString());
                        return session;
                }
            )
        //Récupération des différentes données du produit ayant l'id sélectionné
        .exec(http("Get product")
                            .get("/api/product")
                            .check(jmesPath("[? id == `#{productId}`].categoryId | [0]").find().saveAs("productCategoryId"))
                            .check(jmesPath("[? id == `#{productId}`].name | [0]").find().saveAs("productName"))
                            .check(jmesPath("[? id == `#{productId}`].image | [0]").find().saveAs("productImage"))
                            .check(jmesPath("[? id == `#{productId}`].description | [0]").find().saveAs("productDescription"))
                            .check(jmesPath("[? id == `#{productId}`].price | [0]").find().saveAs("productPrice")));

    private static ChainBuilder update =
                exec(Authentication.authenticate)
              //Affichage des données du produit selectionné
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
                                //Génération d'un nom testX avec X un nombre alatoire entre 0 et 9
                                //Comparaison avec l'ancien nom jusqu'à obtenir un nouveau nom différent
                                String newName;
                                do{
                                        int alea = new Random().nextInt(10);
                                        newName = "test" + alea;
                                }while(newName.equals(session.get("productName").toString()));
                          
                                //Génération d'un nombre aléatoire entre 0 et 9
                                int nombreAlea = new Random().nextInt(10);

                                //Définition d'une liste avec 2 possibilités
                                List<String> listBool = Arrays.asList("true", "false");
                                //Choix aléatoire d'un élément de la liste (marche aussi avec plus d'éléments dans la liste)
                                String bool = listBool.get(new Random().nextInt(listBool.size()));

                                //Affichage des données générées
                                System.out.println("Donnees modifees :"
                                + "\n" + "New Name : " + newName.toString()
                                + "\n" + "Nombre aleatoire : " + nombreAlea
                                + "\n" + "boolean : " + bool.toString());

                                //Enregistrement des nouvelles données dans la session
                                return session
                                        .set("newProductName", newName)
                                        .set("newProductCategory", nombreAlea)
                                        .set("newBool", bool);
                        }
                )       
                //Requête post permettant de modifier le produit (voir le json dans le même dossier)
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
          //Utilisation d'une boucle doIf then pour lancer l'execution des chainbuilder create (si newBool = true alors les chainbuilder se lancent
          .doIf(session -> session.getBoolean("newBool"))
          .then(repeat(3).on(exec(Products.create)))
          .pause(2)
          .exec(Categories.update);

  {
    setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
