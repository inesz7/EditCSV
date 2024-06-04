package gatlingdemostoreapi;

import java.util.*;

import gatlingdemostoreapi.Authentication;
import gatlingdemostoreapi.Headers;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.LocalDateTime;

public class Products {

    public static FeederBuilder.Batchable<String> productsFeeder = csv("data/products.csv").circular();

    public static ChainBuilder list = exec(http("List products")
            .get("/api/product")
            .check(jmesPath("[*].id").ofList().saveAs("allProductIds")));

    public static ChainBuilder get = exec(session -> {
        List<Integer> allProductIds = session.getList("allProductIds");
        return session.set("productId", allProductIds.get(new Random().nextInt(allProductIds.size())));
    })
            .exec(
                    session -> {
                        System.out.println("allProductIds captured:" + session.get("allProductIds").toString());
                        System.out.println("ProductIds selected:" + session.get("productId").toString());
                        return session;
                    })
            .exec(http("Get product")
                    .get("/api/product")
                    .check(jmesPath("[? id == `#{productId}`].categoryId | [0]").saveAs("productCategoryId"))
                    .check(jmesPath("[? id == `#{productId}`].name | [0]").find().saveAs("productName"))
                    .check(jmesPath("[? id == `#{productId}`].image | [0]").find().saveAs("productImage"))
                    .check(jmesPath("[? id == `#{productId}`].description | [0]").find().saveAs("productDescription"))
                    .check(jmesPath("[? id == `#{productId}`].price | [0]").find().saveAs("productPrice")));

    public static ChainBuilder update = exec(Authentication.authenticate)
            .exec(
                    session -> {
                        System.out.println("Product captured:"
                                + "\n" + "Product category: " + session.get("productCategoryId")
                                + "\n" + "Product name: " + session.get("productName").toString()
                                + "\n" + "Product description: " + session.get("productDescription").toString()
                                + "\n" + "Product image: " + session.get("productImage").toString()
                                + "\n" + "Product price: " + session.get("productPrice").toString()
                                + "\n" + "Product id: " + session.get("productId").toString());
                        int cateId = session.getInt("productCategoryId");
                        cateId = cateId + 1;
                        System.out.println("Nouvelle cate : " + cateId);
                        return session.set("productCategoryId", cateId);
                    })
            .exec(
                    session -> {
                        String newName;
                        do {
                            int alea = new Random().nextInt(10);
                            newName = "test" + alea;
                        } while (newName.equals(session.get("productName").toString()));

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
                    })
            .exec(
                    session -> {
                        int newNombre = session.getInt("newProductCategory") + 2;
                        System.out.println("Nouveau nombre :" + newNombre);
                        return session.set("newProductCategory", newNombre);
                    })
            .exec(
                    session -> {
                        String dateString = "20/05/2024 12:00:00";
                        return session.set("date", dateString);
                    })
            .exec(
                    session -> {
                        String newDate = session.getString("date");
                        String[] parts = newDate.split("[ /:]");

                        int day = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        int year = Integer.parseInt(parts[2]);
                        int hour = Integer.parseInt(parts[3]);
                        int minute = Integer.parseInt(parts[4]);
                        int second = Integer.parseInt(parts[5]);

                        LocalDateTime currentDateTime = LocalDateTime.now();

                        int currentDay = currentDateTime.getDayOfMonth();
                        int currentMonth = currentDateTime.getMonthValue();
                        int currentYear = currentDateTime.getYear();
                        int currentHour = currentDateTime.getHour();
                        int currentMinute = currentDateTime.getMinute();
                        int currentSecond = currentDateTime.getSecond();

                        System.out.println("Date initiale: " + newDate);

                        int temp = day;

                        if (year < currentYear) {
                            day++;
                        } else if (year == currentYear) {
                            if (month < currentMonth) {
                                day++;
                            } else if (month == currentMonth) {
                                if (day < currentDay) {
                                    day++;
                                }
                            }
                        }

                        if (day > 28) {
                            day = 1;
                            month++;
                            if (month > 12) {
                                month = 1;
                                year++;
                            }
                        }

                        if (temp != day) {
                            newDate = String.format("%02d/%02d/%04d %02d:%02d:%02d", day, month, year, hour, minute,
                                    second);

                            System.out.println("Nouvelle date: " + newDate);

                            return session.set("date", newDate);
                        } else {
                            return session;
                        }
                    })
            .doIf(session -> session.getBoolean("newBool")).then(
                    exec(session -> {
                        System.out.println("Condition verifiee!!");
                        return session;
                    })

            )
            .exec(http("Update product #{productName}")
                    .put("/api/product/#{productId}")
                    .headers(Headers.authorizationHeaders)
                    .body(ElFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json")));

    public static ChainBuilder create = exec(Authentication.authenticate)
            .feed(productsFeeder)
            .exec(http("Create product #{productName}")
                    .post("/api/product")
                    .headers(Headers.authorizationHeaders)
                    .body(ElFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json")));

    public static ChainBuilder checkProductId = exec(
            session -> {
                int productcheck = 10 + new Random().nextInt(30);
                System.out.println("allProductIds captured:" + session.get("allProductIds").toString());
                System.out.println("Random Id :" + productcheck);
                return session.set("checkId", productcheck);
            })
            .exec(http("Get product")
                    .get("/api/product/#{checkId}")
                    .check(status().is(200))
                    .check((status().saveAs("status"))));
}
