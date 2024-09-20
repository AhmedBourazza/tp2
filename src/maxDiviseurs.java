import java.util.Timer;

public class maxDiviseurs {

    public static void main(String[] args) {

        final int MAX = 25000;

        int N;
        int maxDiviseurs = 1;
        int nombreAvecMax = 1;
        long tDebut = System.currentTimeMillis();

        for (N = 2; N <= MAX; N++) {
            int nbrDiv = 2;  // On commence à 2 pour inclure 1 et N

            for (int i = 2; i < N; i++) {
                if (N % i == 0) {
                    nbrDiv++;
                }
            }

            if (nbrDiv > maxDiviseurs) {
                maxDiviseurs = nbrDiv;
                nombreAvecMax = N;
            }
        }

        // Affichage des résultats
        System.out.println("Le nombre avec le maximum de diviseurs entre 1 et " + MAX + " est : " + nombreAvecMax);
        System.out.println("Ce nombre a " + maxDiviseurs + " diviseur(s).");

        long tempsEcoule = System.currentTimeMillis() - tDebut;
        System.out.println("Temps écoulé pour le calcul : " + tempsEcoule + " ms.");
    }
}
