import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class maxDiviseursAvecThreadPool {
    private static ConcurrentLinkedQueue<SousTache> tacheQueue;
    private static LinkedBlockingQueue<Resultat> resultatQueue;

    private static class SousTache {
        int min, max; // début et fin de la plage des entiers à traiter

        public SousTache(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public void calcul() {
            int maxDiviseursTh = 0;
            int nombreAvecMaxTh = 0;
            for (int N = min; N <= max; N++) {
                int nbrDiv = diviseurCompteur(N);
                if (nbrDiv > maxDiviseursTh) {
                    maxDiviseursTh = nbrDiv;
                    nombreAvecMaxTh = N;
                }
            }
            // Ajouter le résultat dans la queue resultatQueue
            resultatQueue.add(new Resultat(maxDiviseursTh, nombreAvecMaxTh));
        }
    }

    // Classe pour représenter le résultat d'une sous-tâche.
    private static class Resultat {
        int maxDiviseurParSousTache; // Nombre maximal de diviseurs.
        int nombreAvecMaxSousTache; // Quel entier a donné ce nombre maximal.

        public Resultat(int maxDiviseurParSousTache, int nombreAvecMaxSousTache) {
            this.maxDiviseurParSousTache = maxDiviseurParSousTache;
            this.nombreAvecMaxSousTache = nombreAvecMaxSousTache;
        }
    }

    private static class DiviseurCompteurThread extends Thread {
        public void run() {
            while (true) {
                SousTache sousTache = tacheQueue.poll();
                if (sousTache == null) {
                    break; // Sortir si aucune tâche n'est disponible
                }
                sousTache.calcul(); // Effectuer le calcul
            }
        }
    }

    private static void diviseurCompteurAvecThreadPool(int numberOfThreads) {
        long tDebut = System.currentTimeMillis();

        resultatQueue = new LinkedBlockingQueue<>();
        tacheQueue = new ConcurrentLinkedQueue<>();

        // Créer des sous-tâches
        int MAX = 25000;
        int tailleSousTache = 1000;
        for (int i = 1; i <= MAX; i += tailleSousTache) {
            int fin = Math.min(i + tailleSousTache - 1, MAX);
            tacheQueue.add(new SousTache(i, fin));
        }

        // Créer et démarrer les threads
        DiviseurCompteurThread[] threads = new DiviseurCompteurThread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new DiviseurCompteurThread();
            threads[i].start();
        }

        // Attendre la fin des threads
        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Calculer le résultat final
        int maxDiviseurs = 0;
        int nombreAvecMax = 0;
        try {
            while (!resultatQueue.isEmpty()) {
                Resultat resultat = resultatQueue.take();
                if (resultat.maxDiviseurParSousTache > maxDiviseurs) {
                    maxDiviseurs = resultat.maxDiviseurParSousTache;
                    nombreAvecMax = resultat.nombreAvecMaxSousTache;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long tempsEcoule = System.currentTimeMillis() - tDebut;

        // Imprimer le résultat final
        System.out.println("Nombre avec le maximum de diviseurs : " + nombreAvecMax);
        System.out.println("Nombre de diviseurs : " + maxDiviseurs);
        System.out.println("Temps écoulé pour le calcul : " + tempsEcoule + " ms.");
    }

    public static void main(String[] args) {
        Scanner clavier = new Scanner(System.in);
        System.out.print("Entrez le nombre de threads (entre 1 et 100) : ");
        int nombreDeThread = clavier.nextInt();
        diviseurCompteurAvecThreadPool(nombreDeThread);
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
