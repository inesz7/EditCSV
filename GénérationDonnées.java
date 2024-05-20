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
                //Incrémentation des jours
                day++;
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
                //Enregistrement de la nouvelle date
                return session.set("date", newDate);
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
