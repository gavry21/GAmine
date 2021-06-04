package sample.algorithm;

import sample.genetic.Chromosomes;

import java.util.ArrayList;
import java.util.Random;

public class Crossovers {
    public static Random rand = new Random();

    public static ArrayList<Chromosomes> Crossover(Chromosomes p1, Chromosomes p2, String type) {
        ArrayList<Chromosomes> ret = new ArrayList<>();
        Chromosomes r1 = new Chromosomes(p1);
        Chromosomes r2 = new Chromosomes(p2);

        int number_of_genes = p1.size();
        int number_of_bits_in_genes = p1.chrom.get(0).size();
        int total_bits_in_chromosome = number_of_genes * number_of_bits_in_genes;

        int randomInt1 = rand.nextInt(((total_bits_in_chromosome - 1) - 0) + 1) + 0;
        int randomInt2 = rand.nextInt(((total_bits_in_chromosome - 1) - 0) + 1) + 0;

        if (randomInt1 == randomInt2) {
            randomInt2 = (randomInt1 + 10) % total_bits_in_chromosome;
        }
        int max = randomInt1;
        int min = randomInt2;
        if (randomInt1 < randomInt2) {
            max = randomInt2;
            min = randomInt1;
        }

        if (type.equals("SinglePoint")) {
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (index >= randomInt1) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        int temp2 = r2.chrom.get(g).gene.get(b);

                        r1.chrom.get(g).gene.set(b, temp2);
                        r2.chrom.get(g).gene.set(b, temp1);
                    }
                    index++;
                }
            }
        } else if (type.equals("TwoPoint")) {
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (index >= min && index <= max) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        int temp2 = r2.chrom.get(g).gene.get(b);

                        r1.chrom.get(g).gene.set(b, temp2);
                        r2.chrom.get(g).gene.set(b, temp1);
                    }
                    index++;
                }
            }
        }
        ret.add(r1);
        ret.add(r2);

        return ret;
    }
}
