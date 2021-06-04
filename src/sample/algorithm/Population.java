package sample.algorithm;

import sample.genetic.Chromosomes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//популяции
public class Population {
    public ArrayList<Chromosomes> chromosomes = new ArrayList<>();

    public Population() {
        this.chromosomes = new ArrayList<>();
    }

    public Population(ArrayList<Chromosomes> chromosomes) {
        for (int i = 0; i < chromosomes.size(); i++) {
            Chromosomes c = new Chromosomes(chromosomes.get(i));
            this.add(c);
        }
    }


    public void add(Chromosomes c) {
        chromosomes.add(c);
    }

    public int size() {
        return this.chromosomes.size();
    }

    public int indexOf(Chromosomes c) {
        return this.chromosomes.indexOf(c);
    }

    public void remove(int index) {
        this.chromosomes.remove(index);
    }

    public Chromosomes get(int i) {
        return chromosomes.get(i);
    }


    public Population addTwoPopulation(Population p1, Population p2) {
        Population ret = new Population();

        for (int i = 0; i < p1.size(); i++) {
            Chromosomes c = new Chromosomes(p1.get(i));
            ret.add(c);
        }

        for (int i = 0; i < p2.size(); i++) {
            Chromosomes c = new Chromosomes(p2.get(i));
            ret.add(c);
        }
        return ret;
    }

    public Population sortByFitness(Population p1) {
        ArrayList<Chromosomes> ret = new ArrayList<>();

        for (int i = 0; i < p1.chromosomes.size(); i++) {
            Chromosomes c = new Chromosomes(p1.get(i));
            ret.add(c);
        }

        Collections.sort(ret, new Comparator<Chromosomes>() {
            @Override
            public int compare(Chromosomes p1, Chromosomes p2) {
                float p1f = p1.fitness;
                float p2f = p2.fitness;
//возвращаем параметры соответствующие состоянию прямоугольника
                if (p1f > p2f) {
                    return 1;
                } else if (p1f < p2f) {
                    return -1;
                } else {
                    return 0; // Возрастание
                }
            }
        });

        ret.sort(Comparator.comparingDouble(Chromosomes::getFitness)); //от мал до бол

        Collections.reverse(ret);
        return new Population(ret);
    }
}
