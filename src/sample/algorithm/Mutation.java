package sample.algorithm;

import sample.genetic.Chromosomes;

import java.util.ArrayList;
import java.util.Random;

public class Mutation {

    public static Random rand = new Random();

    public static Chromosomes Mutation(Chromosomes individual, String type) {
        Chromosomes r1 = new Chromosomes(individual);

        int number_of_genes = r1.size();
        int number_of_bits_in_genes = r1.chrom.get(0).size();
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

        if (type.equals("Multiple")) {
            ArrayList<Integer> range = new ArrayList<Integer>();
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (min <= index && max >= index) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        r1.chrom.get(g).gene.set(b, (temp1 + 1) % 2);
                    }
                    index++;
                }
            }

            return r1;
        }
        if (type.equals("Flip")) {
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (min <= index && max >= index) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        r1.chrom.get(g).gene.set(b, (temp1 + 1) % 2); //флиппинг
                    }
                    index++;
                }
            }
            return r1;
        }
        if (type.equals("Inversion")) {
            ArrayList<Integer> range = new ArrayList<Integer>();
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (min <= index && max >= index) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        range.add(temp1);
                    }
                    index++;
                }
            }
            index = 0;
            int counter = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (min <= index && max >= index) {
                        int val = range.get(range.size() - 1 - counter);
                        r1.chrom.get(g).gene.set(b, val);
                        counter++;
                    }
                    index++;
                }
            }
            return r1;
        }

        if (type.equals("SingleBitFlip")) {
            int index = 0;
            for (int g = 0; g < number_of_genes; g++) {
                for (int b = 0; b < number_of_bits_in_genes; b++) {
                    if (randomInt1 == index) {
                        int temp1 = r1.chrom.get(g).gene.get(b);
                        r1.chrom.get(g).gene.set(b, (temp1 + 1) % 2); //флиппинг
                        return r1;
                    }
                    index++;
                }
            }
            System.out.println("Проблемы в 1 мутации");
            return r1;
        } else {
            System.out.println("Проблемы в 1 мутации");
            return individual;
        }
    }
}
