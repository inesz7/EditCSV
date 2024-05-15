#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

int main() {

    FILE *file = fopen("student.csv", "w");

    if (file == NULL) {
        perror("Erreur lors de l'ouverture du fichier");
        return EXIT_FAILURE;
    }

    fprintf(file, "category1,category2,category3,category4,category5,category6,category7,category8,category9,category10,category11,category12,category13\n");

    char *liste_cat1[] = {"papa", "maman", "fils", "fille"};
    char *liste_cat2[] = {"papa", "maman", "fils", "fille"};
    char *liste_cat6[] = {"papa", "maman", "fils", "fille"};
    char *liste_cat8[] = {"papa", "maman", "fils", "fille"};
    char *liste_cat9[] = {"papa", "maman", "fils", "fille"};
    char *liste_cat10[] = {"papa", "maman", "fils", "fille"};

    int indice_cat1;
    int indice_cat2;
    int indice_cat6;
    int indice_cat8;
    int indice_cat9;
    int indice_cat10;

    char *date_cat5 = "01-08-2022";
    char *date_cat11 = "01-08-2022T14:30:00";

    int nombre_cat3;
    int nombre_cat4;
    int nombre_cat7;

    char cat7[20];

    srand(time(NULL));

    for(int i=0; i<200; i++){
        nombre_cat3 = rand() % 10000;
        nombre_cat4 = rand() % 10000;
        nombre_cat7 = rand() % 10;

        indice_cat1 = rand() % 4;
        indice_cat2 = rand() % 4;
        indice_cat6 = rand() % 4;
        indice_cat8 = rand() % 4;
        indice_cat9 = rand() % 4;
        indice_cat10 = rand() % 4;

        sprintf(cat7, "test%d", nombre_cat7);

        fprintf(file, "%s,%s,%d,%d,%s,%s,%s,%s,%s,%s,%s,0,0\n", liste_cat1[indice_cat1], liste_cat2[indice_cat2], nombre_cat3, nombre_cat4, date_cat5, liste_cat6[indice_cat6], cat7, liste_cat8[indice_cat8], liste_cat9[indice_cat9], liste_cat10[indice_cat10],
 date_cat11);

    }

    fclose(file);
    return EXIT_SUCCESS;

}
