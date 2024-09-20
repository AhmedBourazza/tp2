import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class maxDiviseursAvecExecutor {
    private final static int MAX = 25000;

    // Classe pour représenter le résultat d'une sous-tâche.
    private static class Resultat {
        int maxDiviseurParSousTache; // Nombre maximal de diviseurs.
        int nombreAvecMaxSousTache; // Quel entier a donné ce nombre maximal.

        public Resultat(int maxDiviseurParSousTache, int nombreAvecMaxSousTache) {
            this.maxDiviseurParSousTache = maxDiviseurParSousTache;
            this.nombreAvecMaxSousTache = nombreAvecMaxSousTache;
        }
    }

    private static class SousTache implements Callable<Resultat> {
        int min, max; // Début et fin de la plage des entiers à traiter

        public SousTache(int min, int max) {
            this.min = min;
            this.max = max;
        }

        // La sous-tâche est exécutée lorsque la méthode call() est appelée
        public Resultat call() {
            int maxDiviseursTh = 0;
            int nombreAvecMaxTh = 0;

            for (int N = min; N <= max; N++) {
                int nbrDiv = diviseurCompteur(N);
                if (nbrDiv > maxDiviseursTh) {
                    maxDiviseursTh = nbrDiv;
                    nombreAvecMaxTh = N;
                }
            }
            return new Resultat(maxDiviseursTh, nombreAvecMaxTh);
        }
    }

    private static void diviseurCompteurAvecExecutor(int nombreDeThread) {
        long tDebut = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(nombreDeThread);
        List<Future<Resultat>> resultats = new ArrayList<>();

        // Créer les sous-tâches et les ajouter à l'executor. Chaque sous-tâche traite une plage de 1000 entiers.
        int tailleSousTache = 1000;
        for (int i = 1; i <= MAX; i += tailleSousTache) {
            int fin = Math.min(i + tailleSousTache - 1, MAX);
            SousTache sousTache = new SousTache(i, fin);
            Future<Resultat> future = executor.submit(sousTache);
            resultats.add(future); // Ajouter l'objet Future à la liste
        }

        // Obtenir les résultats et combiner-les pour produire le résultat final
        int maxDiviseurs = 0;
        int nombreAvecMax = 0;

        for (Future<Resultat> res : resultats) {
            try {
                Resultat resultat = res.get(); // Obtenir le résultat de la Future
                if (resultat.maxDiviseurParSousTache > maxDiviseurs) {
                    maxDiviseurs = resultat.maxDiviseurParSousTache;
                    nombreAvecMax = resultat.nombreAvecMaxSousTache;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long tempsEcoule = System.currentTimeMillis() - tDebut;

        // Imprimer le résultat final
        System.out.println("Nombre avec le maximum de diviseurs : " + nombreAvecMax);
        System.out.println("Nombre de diviseurs : " + maxDiviseurs);
        System.out.println("Temps écoulé pour le calcul : " + tempsEcoule + " ms.");

        // Terminer l'executor.
        executor.shutdown();
    }

    public static void main(String[] args) {
        Scanner clavier = new Scanner(System.in);
        System.out.print("Entrez le nombre de threads (entre 1 et 100) : ");
        int nombreDeThread = clavier.nextInt();
        diviseurCompteurAvecExecutor(nombreDeThread);
    }

    public static int diviseurCompteur(int N) {
        int nbrDiv = 0;
        for (int i = 1; i <= N; i++) {
            if (N % i == 0) {
                nbrDiv++;
            }
        }
        return nbrDiv;
    }
}
