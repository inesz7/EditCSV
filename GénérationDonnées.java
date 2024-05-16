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
