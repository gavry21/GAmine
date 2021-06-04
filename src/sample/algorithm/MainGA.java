package sample.algorithm;

import org.knowm.xchart.AnnotationTextPanel;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.internal.chartpart.Chart;
import sample.Form;
import sample.algorithm.Crossovers;
import sample.algorithm.Mutation;
import sample.algorithm.Population;
import sample.algorithm.Selection;
import sample.genetic.Chromosomes;
import sample.genetic.Genes;
import sample.items.Bin;
import sample.items.Rectangl;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class MainGA extends JPanel
{

    public static Random rand = new Random();
    public static ArrayList<Bin> Bins=new ArrayList<>();
    public static ArrayList<Rectangl> Items=new ArrayList<>();

    public static ArrayList<Rectangl> decreasingItemsSortedByWidth =new ArrayList<>();
    public static ArrayList<Rectangl> decreasingItemsSortedByHeight=new ArrayList<>();
    public static ArrayList<Rectangl> increasingItemsSortedByWidth =new ArrayList<>();
    public static ArrayList<Rectangl> increasingItemsSortedByHeight =new ArrayList<>();
    public static ArrayList<Rectangl> increasingItemsSortedBySpace =new ArrayList<>();
    public static ArrayList<Rectangl> decreasingItemsSortedBySpace =new ArrayList<>();

    public static ArrayList<Bin> decreasingBinsSortedBySpace =new ArrayList<>();       //надо обновлять каждый раз
    public static ArrayList<Bin> decreasingBinsSortedByCPULeft =new ArrayList<>();       //надо обновлять каждый раз
    public static ArrayList<Bin> decreasingBinsSortedByMemoryLeft =new ArrayList<>();       //надо обновлять каждый раз

    public static String XORtype="TwoPoint";
    public static String mutationType="Inversion";
    public static String selectionType="RouletteWheel";


    public static float crossoverProbability=0.9f;
    public static float mutationProbability=0.05f;

    public static int population=100;
   // public static int consecutive_generations=10;
    public static double minFitnessDifference=0.5;
    public static int  maximumGenesrations=100;

    public static int numberOfBitsInGenes=5;
    public static int number_of_allocations=0;


    public static String individualHeuristic="NAN";
    public static String individualParameter="NAN";

    public long m = System.currentTimeMillis();



//Назначение пар
    public static class assignmentPair
    {
        public int tID;
        public int mID;
        public assignmentPair(int t, int m)
        {
            this.tID=t;
            this.mID=m;
        }
    }
//Формирование пар с помощью алгоритма
    public static class PairU
    {
        public Bin m;
        public Rectangl t;
        public int nextFitOffset=0;

        public PairU(Bin m, Rectangl t, int n)
        {
            this.m=m;
            this.t=t;
            this.nextFitOffset=n;
        }
    }

    public static Population generateRandomPopulation(int numberOfIndividuals)
    {
        Population population=new Population();

        for (float p=0; p<numberOfIndividuals; p++)
        {
            Chromosomes c = new Chromosomes();
//Формирование генов
//Берем остаток от деления на 2 и записываем в ген - получаем бинарное представление гена
            for (int i = 0; i < number_of_allocations; i++) {
                Genes g = new Genes();
                for (int j = 0; j < numberOfBitsInGenes; j++) {
                    int random = (int) (Math.random() * 10 + 1);
                    g.add(random % 2);
                }
//После формирования гена он помещается в хромосому
                c.add(g);
            }
            //c.fitness=individualFitness(c);
            c.bins=individualFitness(c);
            c.fitness=1.0f/c.bins;

            population.add(c);
        }
        return population;
    }


    public static float averageFitness(Population population)
    {
        float fitness=0;
        for(int i=0; i<population.size();i++)
        {
            fitness=fitness+population.get(i).fitness;
        }
        return fitness/population.size();
    }


    public static PairU itemAllocator(int HeuristicCode, int parameter, ArrayList<Rectangl> AllocatedItem, PairU p)
    {
        Rectangl itemToBeAssigned=p.t;

        // item itemToBeAssigned=null;
        if(parameter==0 || parameter==11) //decreasingitemsSortedBySpace
        {
            for(int t=0; t<decreasingItemsSortedBySpace.size(); t++)
            {
                if(!Rectangl.ContainsRectangles(AllocatedItem,decreasingItemsSortedBySpace.get(t)))
                {
                    itemToBeAssigned=decreasingItemsSortedBySpace.get(t);
                    break;
                }
            }
        }
        else if(parameter==1)//increasingitemsSortedBySpace
        {
            for(int t=0; t<increasingItemsSortedBySpace.size(); t++)
            {
                if(!Rectangl.ContainsRectangles(AllocatedItem,increasingItemsSortedBySpace.get(t)))
                {
                    itemToBeAssigned=increasingItemsSortedBySpace.get(t);
                    break;
                }
            }
        }
        else if(parameter==10) //notsorted
        {
            for(int t=0; t<Items.size(); t++)
            {
                if(!Rectangl.ContainsRectangles(AllocatedItem,Items.get(t)))
                {
                    itemToBeAssigned=Items.get(t);
                    break;
                }
            }
        }

        return (new PairU(p.m,itemToBeAssigned,p.nextFitOffset));
        //  return itemToBeAssigned;
    }

    public static PairU BinFinder(int HeuristicCode, ArrayList<Bin> openBins,ArrayList<Bin> notopenBins, ArrayList<Rectangl> AllocatedItem, PairU p)
    {


        boolean NotFoundInOpen=true;

        Rectangl itemToBeAssigned=p.t;
        Bin BinToAllocate=p.m;
        int nextFitOffset=p.nextFitOffset;

        if(HeuristicCode==0)                //FirstFit
        {
            for(int m=0; m<openBins.size(); m++)
            {
                if(Bin.FitsToBin(openBins.get(m),itemToBeAssigned))
                {
                    BinToAllocate=openBins.get(m);
                    NotFoundInOpen=false;
                    break;
                }
            }
        }
        else if(HeuristicCode==1 || HeuristicCode==11 || HeuristicCode==111 )           //BestFit & WorstFit & AlmostWorstFit
        {
            ArrayList<Bin> openBinsSortedBySpace=Bin.BinSorter(openBins,"All", "decreasing");
            if(HeuristicCode==1)
            {
                for (int m = 0; m < openBinsSortedBySpace.size(); m++) {

                    if(Bin.FitsToBin(openBinsSortedBySpace.get(m),itemToBeAssigned))
                    {
                        BinToAllocate = openBinsSortedBySpace.get(m);
                        NotFoundInOpen = false;
                        break;
                    }
                }
            }
            if(HeuristicCode==11 || HeuristicCode==111)
            {
                int counter=0;
                for (int m = openBinsSortedBySpace.size()-1; m >= 0; m--)
                {

                    if(Bin.FitsToBin(openBinsSortedBySpace.get(m), itemToBeAssigned))
                    {
                        counter++;
                        BinToAllocate = openBinsSortedBySpace.get(m);
                        NotFoundInOpen = false;
                        if(HeuristicCode==11)
                            break;
                        else if(HeuristicCode==111 && counter==2)
                            break;
                    }
                }
            }
        }
        else if(HeuristicCode==10)          //NextFit
        {
            for(;nextFitOffset<openBins.size();nextFitOffset++)
            {
                if(Bin.FitsToBin(openBins.get(nextFitOffset),itemToBeAssigned))
                {
                    BinToAllocate=openBins.get(nextFitOffset);
                    NotFoundInOpen=false;
                    break;
                }
            }
        }
        else if(HeuristicCode==100)          //Filler
        {
            for(int i=0; i<openBins.size(); i++)
            {
                for(int t=0; t<decreasingItemsSortedBySpace.size(); t++)
                {
                    Rectangl item=decreasingItemsSortedBySpace.get(t);
                    if(!Rectangl.ContainsRectangles(AllocatedItem,item))
                    {
                        if(Bin.FitsToBin(openBins.get(i),item))
                        {
                            BinToAllocate=openBins.get(i);
                            NotFoundInOpen=false;
                            itemToBeAssigned=item;
                            break;
                        }
                    }
                }
            }
        }
        else if(HeuristicCode==101 )          //Djang and Fitch (DJD).
        {
            ArrayList<Bin> openBinsSortedBySpace=Bin.BinSorter(openBins,"All", "increasing");

            float waste=1000000000;

            for(int i=0; i<openBinsSortedBySpace.size(); i++)
            {
                for(int t=0; t<decreasingItemsSortedBySpace.size(); t++)
                {
                    Rectangl tt=decreasingItemsSortedBySpace.get(t);
                    Bin mm=openBinsSortedBySpace.get(i);

                    if(!Rectangl.ContainsRectangles(AllocatedItem,tt))
                    {
                        if(Bin.FitsToBin(mm,tt))
                        {
                            float wasteN=mm.totalCapacityLeft-(tt.heightRequest*tt.widthRequest);
                            if(waste>wasteN)
                            {
                                BinToAllocate=mm;
                                itemToBeAssigned=tt;
                                NotFoundInOpen=false;
                                waste=wasteN;
                            }
                        }
                    }
                }
            }
        }
        else if(HeuristicCode==110)                //Lastfit
        {
            for(int m=openBins.size()-1; m>=0; m--)
            {
                if(Bin.FitsToBin(openBins.get(m),itemToBeAssigned))
                {
                    BinToAllocate=openBins.get(m);
                    NotFoundInOpen=false;
                    break;
                }
            }
        }

        if(NotFoundInOpen)
        {
            for (int m = 0; m < notopenBins.size(); m++)
            {
                if(Bin.FitsToBin(notopenBins.get(m), itemToBeAssigned))
                {
                    BinToAllocate = notopenBins.get(m);
                    break;
                }
            }
        }

        if(BinToAllocate==null)
        {

            for(int i=0; i<notopenBins.size(); i++)
            {
                if(notopenBins.get(i).widthLeft>itemToBeAssigned.widthRequest && notopenBins.get(i).heightLeft>itemToBeAssigned.heightRequest)
                    System.out.println(notopenBins.get(i).binID);
            }
            System.out.println(itemToBeAssigned.heightRequest+"\t"+itemToBeAssigned.widthRequest);
            System.out.println("Cant Find Bin");
        }
        return (new PairU(BinToAllocate,itemToBeAssigned,nextFitOffset));
    }

    public static ArrayList<Integer> applyOneHeuristic(int H, int p)
    {
        int HeuristicCode=H;
        int parameter=p;

        //FFI
        //HeuristicCode=0;
        //parameter=1;
        if(individualHeuristic.equals("FFI"))
            HeuristicCode=0;
        if(individualParameter.equals("FFI"))
            parameter=1;


        //FFD
        //HeuristicCode=0;
        //parameter=0;
        if(individualHeuristic.equals("FFD"))
            HeuristicCode=0;
        if(individualParameter.equals("FFD"))
            parameter=0;

        //FF
        // HeuristicCode=0;
        // parameter=11;
        if(individualHeuristic.equals("FF"))
            HeuristicCode=0;
        if(individualParameter.equals("FF"))
            parameter=11;



        //BFI
        //HeuristicCode=1;
        //parameter=1;
        if(individualHeuristic.equals("BFI"))
            HeuristicCode=1;
        if(individualParameter.equals("BFI"))
            parameter=1;



        //BFD
        //HeuristicCode=1;
        //            parameter=0;
        if(individualHeuristic.equals("BFD"))
            HeuristicCode=1;
        if(individualParameter.equals("BFD"))
            parameter=0;



        //BF
        //  HeuristicCode=1;
        //  parameter=11;
        if(individualHeuristic.equals("BF"))
            HeuristicCode=1;
        if(individualParameter.equals("BF"))
            parameter=11;

        //WFI
        //  HeuristicCode=11;
        //  parameter=1;
        if(individualHeuristic.equals("WFI"))
            HeuristicCode=11;
        if(individualParameter.equals("WFI"))
            parameter=1;


        //WFD
        //HeuristicCode=11;
        //parameter=0;
        if(individualHeuristic.equals("WFD"))
            HeuristicCode=11;
        if(individualParameter.equals("WFD"))
            parameter=0;


        //WF
        //  HeuristicCode=11;
        //  parameter=11;
        if(individualHeuristic.equals("WF"))
            HeuristicCode=11;
        if(individualParameter.equals("WF"))
            parameter=11;


        //AWFI
        //HeuristicCode=111;
        //parameter=1;

        if(individualHeuristic.equals("AWFI"))
            HeuristicCode=111;
        if(individualParameter.equals("AWFI"))
            parameter=1;


        //AWFD
        // HeuristicCode=111;
        // parameter=0;
        if(individualHeuristic.equals("AWFD"))
            HeuristicCode=111;
        if(individualParameter.equals("AWFD"))
            parameter=0;


        //AWF
        //HeuristicCode=111;
        //parameter=11;

        if(individualHeuristic.equals("AWF"))
            HeuristicCode=111;
        if(individualParameter.equals("AWF"))
            parameter=11;


        //NFI
        // HeuristicCode=10;
        // parameter=1;
        if(individualHeuristic.equals("NFI"))
            HeuristicCode=10;
        if(individualParameter.equals("NFI"))
            parameter=1;


        //NFD
        // HeuristicCode=10;
        // parameter=0;

        if(individualHeuristic.equals("NFD"))
            HeuristicCode=10;
        if(individualParameter.equals("NFD"))
            parameter=0;

        //NF
        // HeuristicCode=10;
        // parameter=11;

        if(individualHeuristic.equals("NF"))
            HeuristicCode=10;
        if(individualParameter.equals("NF"))
            parameter=11;

        //Filler
        //HeuristicCode=100;
        if(individualHeuristic.equals("Filler"))
            HeuristicCode=100;


        //Djang and Fitch
        //HeuristicCode=101;
        if(individualHeuristic.equals("DJF"))
            HeuristicCode=101;


        ArrayList<Integer> ret=new ArrayList<>();
        ret.add(HeuristicCode);
        ret.add(parameter);

        return ret;
    }

    public static class individualFitnessRunnable implements Runnable {
        Chromosomes individual;
        String name;
        Thread t;
        public individualFitnessRunnable(Chromosomes individual, String nameOfThread)
        {
            this.individual = individual;
            this.name=nameOfThread;

            t=new Thread(this, name);
            t.start();
        }
        @Override
        public void run() {
            individual.bins=individualFitness(individual);
            individual.fitness=1.0f/individual.bins;
        }
    }

    public static int individualFitness(Chromosomes individual)
    {
        ArrayList<Rectangl> AllocatedItem=new ArrayList<>();
        ArrayList<Bin> openBins=new ArrayList<>();
        ArrayList<Bin> notopenBins=Bin.CopyBins(Bins);

        individual.assignments.clear();

        PairU p=new PairU(null,null, 0);

        for(int i=0; i<number_of_allocations; i=i+1)
        {
            p.m=null;
            p.t=null;

            //получаем бинарный код
            //------------------------------------------------------------------------------//
            Genes gene=individual.get(i);
           // System.out.println("GENE"+ gene.get(0)*10000+gene.get(1)*1000+gene.get(2)*100+gene.get(3)*10+gene.get(4));
            //000->0   001->1   010->10  011->11    100->100, 101->101, 110->110,    111->111
            int HeuristicCode=gene.get(0)*100+gene.get(1)*10+gene.get(2);
            int parameter=gene.get(3)*10+gene.get(4);

            //В переменной HeuristicCode получаем бинарный код из первых трех значений в гене, по которому происходит выбор эвристического алгоритма для упаковки прямоугольнико
            //В переменной parameter получаем бинарный код из последний двух значений в гене, по которому определеяем сортировку прямоугольников
            //------------------------------------------------------------------------------//

            //Находим контейнер и помещаем прямоугольники//
            //------------------------------------------------------------------------------//
            p=itemAllocator(HeuristicCode,parameter,AllocatedItem,p);
            p=BinFinder(HeuristicCode,openBins,notopenBins, AllocatedItem,p);
            //------------------------------------------------------------------------------//

            Rectangl itemToBeAssigned=p.t;
            Bin BinToAllocate=p.m;

            //make update//
            //------------------------------------------------------------------------------//
            AllocatedItem.add(itemToBeAssigned);

            BinToAllocate=Bin.UpdateBin(BinToAllocate,itemToBeAssigned);

            int indexInOpen=Bin.ContainsBin(openBins,BinToAllocate);
            int indexInNotOpen=Bin.ContainsBin(notopenBins,BinToAllocate);
            if(indexInOpen==-1)
            {
                openBins.add(BinToAllocate);
            }
            else
            {
                openBins.set(indexInOpen,BinToAllocate);
            }
            if(indexInNotOpen!=-1)
            {
                notopenBins.remove(indexInNotOpen);
            }
            //------------------------------------------------------------------------------//

            individual.assignments.add(new assignmentPair(itemToBeAssigned.recID,BinToAllocate.binID));

        }


        return openBins.size();
        //float bscore=1.0f/openBins.size();;
        // return bscore;
    }

    public static Population GenesrateNewGenesration(Population generation)
    {
        Population newGenesration=new Population();

        ArrayList<individualFitnessRunnable> threads=new ArrayList<>();

        for(int i=0; i<generation.size()/2; i++)
        {
            Chromosomes parent1=new Chromosomes(Selection.RouletteWheel(generation));
            Chromosomes parent2=new Chromosomes(Selection.RouletteWheel(generation));
//Для турнирного варианта
            // Chromosomes parent1=new Chromosomes(IndividualSelection.Tournament(generation));
            // Chromosomes parent2=new Chromosomes(IndividualSelection.Tournament(generation));

            Chromosomes kid1=new Chromosomes(parent1);
            Chromosomes kid2=new Chromosomes(parent2);
            if(rand.nextFloat()<crossoverProbability)
            {
                ArrayList<Chromosomes> kids = Crossovers.Crossover(parent1,parent2,XORtype);
                kid1 = kids.get(0);
                kid2 = kids.get(1);
            }
            if(rand.nextFloat()<mutationProbability)
            {
                kid1= Mutation.Mutation(kid1,mutationType);
            }
            if(rand.nextFloat()<mutationProbability)
            {
                kid2=Mutation.Mutation(kid2,mutationType);
            }

            individualFitnessRunnable fit1 = new individualFitnessRunnable(kid1, "kid1");
            //new Thread(fit1).start();

            individualFitnessRunnable fit2 = new individualFitnessRunnable(kid2, "kid2");
            //new Thread(fit2).start();

            threads.add(fit1);
            threads.add(fit2);

    /*
           // kid1.fitness=individualFitness(kid1);
           // kid2.fitness=individualFitness(kid2);

            kid1.bins=individualFitness(kid1);
            kid1.fitness=1.0f/kid1.bins;

            kid2.bins=individualFitness(kid2);
            kid2.fitness=1.0f/kid2.bins;

            newGenesration.add(kid1);
            newGenesration.add(kid2);
  */
        }
        for(int i=0; i<threads.size(); i++)
        {
            try {
                threads.get(i).t.join();
                newGenesration.add(threads.get(i).individual);
            }catch (Exception e) { }
        }
        return newGenesration;
    }

    public static Population Selection(Population prevGenesration, Population newGenesration)
    {
        Population ret=prevGenesration.addTwoPopulation(prevGenesration,newGenesration);

        ret=ret.sortByFitness(ret);

        //for (int i=0;i<ret.size();i++)
        //{
        //    System.out.println("fit="+ret.get(i).fitness);
        //}

        Population retf=new Population();

        for(int i=0; i<prevGenesration.size(); i++)
        {
            retf.add(ret.get(i));
        }
        return retf;
    }

    public static ArrayList<assignmentPair> GA(String output_file) throws IOException {
        LinkedList<Float> fitForFunction = new LinkedList<>();
        LinkedList<Integer> generationForFunction = new LinkedList<>();
        try
        {
            FileWriter writer = new FileWriter(output_file);

            float prevAvgFit = 0;
            float newAvgFit = 0;
            int consGenesration = 0;

            Population initialPop = generateRandomPopulation(population);

            Population prevGenesration = initialPop;
            Population newGenesration = initialPop;

            prevAvgFit = averageFitness(prevGenesration);
            newAvgFit = averageFitness(newGenesration);

            int counter = 0;
//Вывод
            System.out.println("Genesration, averageFitness, BestFitness, leastOpenedBins");
            writer.write("Genesration,  averageFitness,  BestFitness, leastOpenedBins\n");
            //while (counter < maximumGenesrations && consGenesration <consecutive_generations)
            while (counter < maximumGenesrations)
            {
                prevAvgFit = newAvgFit;

                newGenesration = GenesrateNewGenesration(prevGenesration);

                newAvgFit = averageFitness(newGenesration);

                prevGenesration = Selection(prevGenesration, newGenesration);

                if(Math.abs(prevAvgFit-newAvgFit)<minFitnessDifference)
                    consGenesration++;
                else
                    consGenesration=0;

                counter++;

                generationForFunction.add(counter);
                fitForFunction.add(prevAvgFit);

               // System.out.println(counter+",      "+prevAvgFit+",       "+prevGenesration.get(0).fitness+",          "+prevGenesration.get(0).bins);
                writer.write(counter+","+prevAvgFit+","+prevGenesration.get(0).fitness+","+prevGenesration.get(0).bins+"\n");
            }
            writer.close();

            Chromosomes best= prevGenesration.get(0);

            //график

            Chart chart = QuickChart.getChart("ГА", "Поколения", String.valueOf(prevGenesration.get(0).fitness), "y(x)", generationForFunction, fitForFunction);
            chart.addAnnotation(
                    new AnnotationTextPanel("Количество использованных контейнеров: " + prevGenesration.get(0).bins, 200, 200, true));
            new SwingWrapper(chart).displayChart();
            // Save it
            BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
            // or save it in high-res
            BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);

            return best.assignments;

        }catch (Exception e){e.printStackTrace();}


        return null;
    }

    public static void main(String[] args) throws Exception
    {
//Пути со значениями
        String output_file = "./output.csv";

        String path_bin = "./input/bins_input.csv";
        String path_item = "./input/rec_input.csv";
        String path_toConfig="./input/params.txt";



        if (args.length > 4)
        {
            path_bin = args[0];
            path_item = args[1];
            path_toConfig = args[2];
            output_file = args[3];
        }


        ReadData(path_bin, path_item, path_toConfig);

        System.out.println("#items="+Items.size());
        System.out.println("#bins="+Bins.size());

        decreasingItemsSortedByWidth = Rectangl.RecSorter(Items, "Width", "decreasing");
        decreasingItemsSortedByHeight = Rectangl.RecSorter(Items, "Height", "decreasing");
        increasingItemsSortedByWidth = Rectangl.RecSorter(Items, "Width", "increasing");
        increasingItemsSortedByHeight = Rectangl.RecSorter(Items, "Height", "increasing");
        increasingItemsSortedBySpace = Rectangl.RecSorter(Items, "All", "increasing");
        decreasingItemsSortedBySpace = Rectangl.RecSorter(Items, "All", "decreasing");



        decreasingBinsSortedBySpace = Bin.BinSorter(Bins, "All", "decreasing");
        decreasingBinsSortedByCPULeft = Bin.BinSorter(Bins, "Width", "decreasing");
        decreasingBinsSortedByMemoryLeft = Bin.BinSorter(Bins, "Height", "decreasing");

        //////
        numberOfBitsInGenes = 5;
        number_of_allocations = Items.size();

        long startTime = System.nanoTime();

        ArrayList<assignmentPair> assignments = GA(output_file);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        System.out.println(duration/1000000);//миллисекунды
    }

    public static void ReaderHelper(String path, int type)
    {
        try
        {
            File file = new File(path);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int id=0;
            while ((line = br.readLine()) != null)
            {
                String splitted[] = line.split(",");
                if (splitted[0].equals("width"))
                    continue;

                int width=Integer.parseInt(splitted[0]);
                int height=Integer.parseInt(splitted[1]);
                int number=Integer.parseInt(splitted[2]);

                for(int i=0; i<number; i++)
                {
                    if(type==1)
                    {
                        Bins.add(new Bin(id, width, height));
                    }
                    else
                    {
                        Items.add(new Rectangl(id, width, height));
                    }
                    id++;
                }
            }
            br.close();
        }catch (Exception e){ System.out.println("Ошибка в чтении файлов в ReaderHelper"); }
    }

    public static void ReadData(String path_bin, String path_item, String path_toConfig)
    {

        try {
            File file = new File(path_toConfig);

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {

                line=line.split(";")[0];

                String argument=line.split("=")[0];
                String paramter=line.split("=")[1];

                if(argument.equals("XORtype"))
                {
                    XORtype=paramter;
                }
                if(argument.equals("mutationType"))
                {
                    mutationType=paramter;
                }
                if(argument.equals("selectionType"))
                {
                    selectionType=paramter;
                }
                if(argument.equals("crossoverProbability"))
                {
                    crossoverProbability=Float.parseFloat(paramter);
                }
                if(argument.equals("mutationProbability"))
                {
                    mutationProbability=Float.parseFloat(paramter);
                }
                if(argument.equals("population"))
                {
                    population=Integer.parseInt(paramter);
                }
                if(argument.equals("mutationProbability"))
                {
                    mutationProbability=Float.parseFloat(paramter);
                }
                if(argument.equals("mutationProbability"))
                {
                    mutationProbability=Float.parseFloat(paramter);
                }
                if(argument.equals("minFitnessDifference"))
                {
                    minFitnessDifference=Float.parseFloat(paramter);
                }
                if(argument.equals("maximumGenesrations"))
                {
                    maximumGenesrations=Integer.parseInt(paramter);
                }

            }
        }catch (Exception e){ System.out.println("ошибка в чтении файлов в configReader"); }


/*
        XORtype="TwoPoint";         //SinglePoint,   TwoPoint
        mutationType="Inversion";   //SingleBitFlip, Inversion, Flip
        selectionType="RouletteWheel";

        crossoverProbability=0.95f;
        mutationProbability=0.05f;
        population=100;
        consecutive_generations=10;
        minFitnessDifference=0.5;
        maximumGenesrations=1;
*/

        ReaderHelper(path_bin,1);
        ReaderHelper(path_item,0);
    }
}
