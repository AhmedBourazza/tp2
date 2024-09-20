import java.util.Scanner;

public class maxDiviseursAvecThreads {

   private final static int MAX = 25000;

   private volatile static int maxDiviseurs = 0;
   private volatile static int nombreAvecMax;

   // Méthode pour combiner les résultats des threads
   private static synchronized void combinerResultat(int maxDiviseurParThread, int nombreAvecMaxParThread) {
      if (maxDiviseurParThread > maxDiviseurs) {
         maxDiviseurs = maxDiviseurParThread;
         nombreAvecMax = nombreAvecMaxParThread;
      } else if (maxDiviseurParThread == maxDiviseurs && nombreAvecMaxParThread < nombreAvecMax) {
         nombreAvecMax = nombreAvecMaxParThread;
      }
   }

   // Classe Thread pour calculer les diviseurs dans une plage donnée
   private static class diviseurCompteurThread extends Thread {
      public int min, max;

      @Override
      public void run() {
         int maxDiviseursTh = 1;
         int nombreAvecMaxTh = 1;

         // Calcul du nombre maximum de diviseurs pour les entiers entre min et max
         for (int N = min; N <= max; N++) {
            int nbrDiv = diviseurCompteur(N);
            if (nbrDiv > maxDiviseursTh) {
               maxDiviseursTh = nbrDiv;
               nombreAvecMaxTh = N;
            }
         }

         // Combiner le résultat avec le résultat global
         combinerResultat(maxDiviseursTh, nombreAvecMaxTh);
      }
   }

   // Fonction pour gérer le calcul avec plusieurs threads
   private static void diviseurCompteurAvecThread(int nombreDeThread) {
      long tDebut = System.currentTimeMillis();

      // Création et initialisation des threads
      diviseurCompteurThread[] th = new diviseurCompteurThread[nombreDeThread];
      int nombreDeNombres = MAX / nombreDeThread;

      for (int i = 0; i < nombreDeThread; i++) {
         th[i] = new diviseurCompteurThread();
         th[i].min = i * nombreDeNombres + 1;
         if (i == nombreDeThread - 1) {
            th[i].max = MAX;
         } else {
            th[i].max = (i + 1) * nombreDeNombres;
         }
      }

      // Démarrage des threads
      for (diviseurCompteurThread div : th) {
         div.start();
      }

      // Attente de la fin des threads
      for (int i = 0; i < nombreDeThread; i++) {
         try {
            th[i].join();
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
         }
      }

      long tempsEcoule = System.currentTimeMillis() - tDebut;
      System.out.println("Temps écoulé: " + tempsEcoule + " ms");
      System.out.println("Nombre avec le maximum de diviseurs: " + nombreAvecMax);
      System.out.println("Nombre de diviseurs: " + maxDiviseurs);
   }

   // Fonction principale
   public static void main(String[] args) {
      Scanner clavier = new Scanner(System.in);
      System.out.print("Entrez le nombre de threads (entre 1 et 100) : ");
      int nombreDeThread = clavier.nextInt();

      // Lancer le calcul avec le nombre de threads spécifié
      diviseurCompteurAvecThread(nombreDeThread);
   }

   // Fonction pour compter les diviseurs d'un nombre N
   public static int diviseurCompteur(int N) {
      int nbrDiv = 0;  // On commence à 0 pour compter tous les diviseurs
      for (int i = 1; i <= N; i++) {  // On commence à 1 et on inclut N
         if (N % i == 0) {
            nbrDiv++;
         }
      }
      return nbrDiv;
   }

}
