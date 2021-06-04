package sample.genetic;

import java.util.ArrayList;

//ген
public class Genes {

    public ArrayList<Integer> gene = new java.util.ArrayList<>();

    public Genes(){

    }
    public Genes(Genes g) {
        for (int i = 0; i < g.size(); i++)
            this.gene.add(g.get(i));
    }

    public void add(int bit) {
        gene.add(bit);
    }

    public int size() {
        return gene.size();
    }

    public int get(int i) {
        return gene.get(i);
    }
}
