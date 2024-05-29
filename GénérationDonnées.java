.exec(
        session -> {
              //Génération d'un nom testX avec X un nombre alatoire entre 0 et 9
              //Comparaison avec l'ancien nom jusqu'à obtenir un nouveau nom différent
              String newName;
              do{
                    int alea = new Random().nextInt(10);
                    newName = "test" + alea;
              }while(newName.equals(session.get("productName").toString()));

              //Enregistrement des nouvelles données dans la session
              return session.set("newProductName", newName):
        }
) 

.exec(
        session -> {                           
              //Génération d'un nombre aléatoire entre 0 et 9
              int nombreAlea = new Random().nextInt(10);

              //Enregistrement des nouvelles données dans la session
              return session.set("newProductCategory", nombreAlea);
       }
) 

.exec(
        session -> {                           
              //Définition d'une liste avec 2 possibilités
              List<String> listBool = Arrays.asList("true", "false");
              //Choix aléatoire d'un élément de la liste (marche aussi avec plus d'éléments dans la liste)
              String bool = listBool.get(new Random().nextInt(listBool.size()));

              //Enregistrement des nouvelles données dans la session
              return session.set("newBool", bool);
       }
) 
                                
.exec(
        session -> {
              //Récupération de l'ancien nombre et ajout de 2 à la valeur
              int newNombre = session.getInt("newProductCategory") + 2;
              //Affichage du nouveau nombre
              System.out.println("Nouveau nombre :" + newNombre);
              //Enregistrement de la nouvelle valeur dans la variable de session
              return session.set("newProductCategory", newNombre);
        }
)

.exec(
                        session -> {
                                //Création de la date
                                String dateString = "01/01/2022 12:00:00";
                                return session.set("date", dateString);
                        }
                )
.exec(
        session -> {
                //Récupération de la date initiale
                String newDate = session.getString("date");
                //Séparation de la date en mois/jours/années heures:minutes:secondes
                String[] parts = newDate.split("[ /:]");

                //Définition des différentes parties
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                int hour = Integer.parseInt(parts[3]);
                int minute = Integer.parseInt(parts[4]);
                int second = Integer.parseInt(parts[5]);

                //Affichage de la date initiale
                System.out.println("Date initiale: " + newDate);

                //Récupération de la date d'aujourd'hui (ne pas oublier de faire : import java.time.LocalDateTime;)
                LocalDateTime currentDateTime = LocalDateTime.now();

                //Séparation des différentes parties
                int currentDay = currentDateTime.getDayOfMonth();
                int currentMonth = currentDateTime.getMonthValue();
                int currentYear = currentDateTime.getYear();
                int currentHour = currentDateTime.getHour();
                int currentMinute = currentDateTime.getMinute();
                int currentSecond = currentDateTime.getSecond();
                
                //On garde une trace du jour initial
                int temp = day;
                
                //On incrélente le jour seulement si on est à une date infèrieure à aujourd'hui
                if(year < currentYear){
                                        day++;
                                }
                                else if(year == currentYear){
                                        if(month < currentMonth){
                                                day++;
                                        }
                                        else if(month == currentMonth){
                                                if(day < currentDay){
                                                        day++;
                                                }
                                        }
                                }
                
                //Faire en sorte que le mois et l'années changent si les jours > 28
                if (day > 28) {
                        day = 1;
                        month++;
                        if (month > 12) {
                                month = 1;
                                year++;
                        }
                }
                
                //Redéfinition de la date avec les nouvelles parties
                newDate = String.format("%02d/%02d/%04d %02d:%02d:%02d", day, month, year, hour, minute, second);
                //Affichage de la nouvelle date
                System.out.println("Nouvelle date: " + newDate);
                
                //Enregistrement de la nouvelle date seulement si la date a changé
                if(temp != day){
                        return session.set("date", newDate);
                }
                else {
                        return session;
                }
        }
)

//Requête get permettant de récupérer le status et de l'enregistrer en tant que "status dans la session" :
        private static ChainBuilder checkProductId = 
                exec(http("Get product")
                        .get("/api/product/#{checkId}")
                        .check(status().is(200))
                        .check((status().saveAs("status"))));

//Boucle doWhileDuring à mettre dans le scenario permettant de run une fois le contenu et de le relancer tant que la condition est respectée (ici tant questatus différent de 200)
//Le 20 après la virgule est la durée maximale en seconde ce qui évite les boucles infinies
.doWhileDuring(session -> session.getInt("status") != 200, 20).on(exec(Products.checkProductId));

//Exemple de get avec ofMap et de récupération des données derrière
private static ChainBuilder get =
            exec(session -> {
              List<Integer> allProductIds = session.getList("allProductIds");
              return session.set("productId", allProductIds.get(new Random().nextInt(allProductIds.size())));
            })
              .exec(http("Get product")
                            .get("/api/product/#{productId}")
                            .check(jmesPath("id").ofInt().isEL("#{productId}"))
                            .check(jmesPath("@").ofMap().saveAs("product")));

private static ChainBuilder update =
                exec(Authentication.authenticate)
              .exec( session -> {
                  Map<String, Object> product = session.getMap("product");
                  return session
                          .set("productCategoryId", product.get("categoryId"))
                          .set("productName", product.get("name"))
                          .set("productDescription", product.get("description"))
                          .set("productImage", product.get("image"))
                          .set("productPrice", product.get("price"))
                          .set("productId", product.get("id"));
              })
                    .exec(http("Update product #{productName}")
                            .put("/api/product/#{productId}")
                            .headers(authorizationHeaders)
                            .body(ElFileBody("gatlingdemostoreapi/demostoreapisimulation/create-product.json"))
                            .check(jmesPath("price").isEL("#{productPrice}")));

//Récupérer un id et lui ajouter 1
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
        //Partie interressante : On récupère l'id et on l'enregistre dans la variable "productCategoryId" (le find servait à rien je l'ai enlever et ça marche sans)
        .exec(http("Get product")
              .get("/api/product")
              .check(jmesPath("[? id == `#{productId}`].categoryId | [0]").saveAs("productCategoryId")));
        .exec(
                session -> {
                        //On print l'id qu'on a récupéré
                        System.out.println("Product captured:"
                        + "\n" + "Product category: " + session.get("productCategoryId"));
                        //On rentre l'id dans une nouvelle variable (l'id enregistré de base est pas un int tout comme toi, du coup tu fais getInt pour le convertir)
                        int categoryId = session.getInt("productCategoryId");
                        //On ajoute 1 au int qu'on a créé
                        categoryId = categoryId + 1;
                        //On print le int pour voir si ça a marché
                        System.out.println("Nouvel Id de categorie : " + cateId);
                        //On enregistre le nouvel id dans la variable de session "productCategoryId"
                        return session.set("productCategoryId", cateId);
                })

//Récupérer la réponse true ou false d'une requête get : 
private static ChainBuilder get =
        //On fait la requête get et on enregistre la réponse dans une variable reponse
        exec(http("Get product")
             .get("/api/product")
             .check(bodystring().saveAs("reponse"))
        .exec(
                session -> {
                        //On print la réponse pour voir ce qu'on a
                        System.out.println("Réponse : " + session.get("reponse"));
                        return session;
                });

//Dans le scénario on met une boucle dowhileduring qui vérifie la réponse et relance la requête get jsuqu'à ce qu'elle soit true
private ScenarioBuilder scn = scenario("DemostoreApiSimulation")
        .doWhileDuring(session -> session.getString("reponse").equals("false"), 20).on(exec(Products.get)):
                        
