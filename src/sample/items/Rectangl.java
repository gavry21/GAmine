package sample.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//прямоугольники
public class Rectangl {
    public int recID;
    public float widthRequest;
    public float heightRequest;
    public boolean Assigned = false;


    public Rectangl(int recID, float widthRequest, float heightRequest) {
        this.recID = recID;
        this.widthRequest = widthRequest;
        this.heightRequest = heightRequest;
        this.Assigned = false;

    }

    public Rectangl(Rectangl rec) {
        this.recID = rec.recID;
        this.widthRequest = rec.widthRequest;
        this.heightRequest = rec.heightRequest;
        this.Assigned = rec.Assigned;
    }

    public float getHeight() {
        return heightRequest;
    }

    public float getWidth() {
        return widthRequest;
    }

    public static boolean ContainsRectangles(ArrayList<Rectangl> recs, Rectangl rec) {
        boolean contains = false;

        for (int i = 0; i < recs.size(); i++) {
            if (recs.get(i).recID == rec.recID) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    public static ArrayList<Rectangl> RecSorter(ArrayList<Rectangl> recs, String type, String typeOfOrder) {
        ArrayList<Rectangl> ret = new ArrayList<>();

        for (int i = 0; i < recs.size(); i++) {
            Rectangl t = new Rectangl(recs.get(i));
            ret.add(t);
        }
        if (type.equals("All")) {
            Collections.sort(ret, new Comparator<Rectangl>() {
                @Override
                public int compare(Rectangl p1, Rectangl p2) {
                    return (int) ((p1.widthRequest * p1.heightRequest) - (p2.widthRequest * p2.heightRequest)); // Возрастание
                }
            });
            ret.sort(Comparator.comparingDouble(Rectangl::getWidth)); //от мал до бол
        }


        if (type.equals("Width")) {
            Collections.sort(ret, new Comparator<Rectangl>() {
                @Override
                public int compare(Rectangl p1, Rectangl p2) {
                    return (int) (p1.widthRequest - p2.widthRequest); // Возрастание
                }
            });
            ret.sort(Comparator.comparingDouble(Rectangl::getWidth)); //от мал до бол
        }

        if (type.equals("Height")) {
            Collections.sort(ret, new Comparator<Rectangl>() {
                @Override
                public int compare(Rectangl p1, Rectangl p2) {
                    return (int) (p1.heightRequest - p2.heightRequest); // Возрастание
                }
            });
            ret.sort(Comparator.comparingDouble(Rectangl::getHeight)); //от мал до бол
        }

        if (typeOfOrder.equals("decreasing")) {
            Collections.reverse(ret);
        }
        return ret;
    }
}
