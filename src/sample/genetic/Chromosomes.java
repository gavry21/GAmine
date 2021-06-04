package sample.genetic;

import sample.algorithm.MainGA;


import java.util.ArrayList;
//хромосомы
public class Chromosomes {

    public ArrayList<Genes> chrom = new java.util.ArrayList<>();
    public float fitness = 0;
    public int bins = 0;

    public ArrayList<MainGA.assignmentPair> assignments=new ArrayList<>();

    public Chromosomes() {
    }

    public float getFitness() {
        return fitness;
    }

    public void add(Genes g)
    {
        chrom.add(g);
    }
    public int size()
    {
        return chrom.size();
    }
    public Genes get(int i)
    {
        return chrom.get(i);
    }

    public Chromosomes(Chromosomes c){
        for(int i=0; i<c.size();i++)
        {
            Genes g=new Genes(c.get(i));
            this.chrom.add(g);
            this.fitness=c.fitness;
            this.bins=c.bins;
        }
        this.assignments.clear();
        for(int j=0; j<c.assignments.size(); j++)
        {
            this.assignments.add(new MainGA.assignmentPair(c.assignments.get(j).tID,c.assignments.get(j).mID));
        }
    }

}
