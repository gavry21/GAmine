package sample.oldVer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.*;

class Child {
    int[] mother;
    int[] father;
    int[] son;
    int[] daughter;

    public Child() {
        var end = Math.floor(Math.random() * mother.length);
        var moth = mother.length;//determines length of mother
        var fath = father.length;
        int son[] = new int[fath];
        int daughter[] = new int[moth];
        for (int i = 0; i < end; i++) {
            son[i] = mother[i];
            daughter[i] = father[i];
        }
    }

    public int[] getDaughter() {
        return daughter;
    }

    public int[] getSon() {
        return son;
    }

    public int[] getFather() {
        return father;
    }

    public int[] getMother() {
        return mother;
    }

    public void setMother(int[] mother) {
        this.mother = mother;
    }

    public void setFather(int[] father) {
        this.father = father;
    }

    public void setDaughter(int[] daughter) {
        this.daughter = daughter;
    }

    public void setSon(int[] son) {
        this.son = son;
    }
}

public class Main extends JPanel{
    // allow chromosomal drift with this range (-0.05, 0.05)
    public static double change(double n, double dr) {
        n += Math.random() * dr;
        if (n<0)
            n = 0;
        return n;
    }

    public static Rectangles map(Rectangles[] entity){
        Rectangles r = null;
        double dir = Math.random();
        for(int i = 0; i<entity.length; i++) {
            if (dir > .0) {
                if (entity[i].x == dir)
                    r.x = change(entity[i].x, dir);
                if (entity[i].y != dir)
                    r.x = change(entity[i].y, dir);
            }
        }
        return r;
    }
    //------------------------------МУТАЦИЯ-----------------------------------//
//получаем дрейф
//создаем новый массив entity с элементами x, y, h, w
//change - для перевода хромосом в значения в промежутке (-0.05; 0.05)
//возвращаем новые значения x и y в copy
    public static Rectangles[] mutation(Rectangles[] entity) {
        double drift = ((Math.random()-0.5)*2) * 0.5;
        Rectangles[] copy = null;
        map(copy);
        while (Math.random() > 0.5) {
            int i = (int) Math.floor(Math.random() * entity.length);
            int j = (int) Math.floor(Math.random() * entity.length);
            copy[i].x = entity[j].x; copy[i].y = entity[j].y;
            copy[j].x = entity[i].x; copy[j].y = entity[i].y;
        }
        return copy;
    };

    //------------------------------КРОССОВЕР-----------------------------------//
// кроссовер - идем по длине матери умноженной на рандом число
//и округляем в меньшую сторону полученный результат
//сын состоит из отца, а дочь из матери на анчальном этапе
//далее оставшимся элементам присваиваем соответствующие элементы уже других
//родителей пока не дойдем до конца массива end
//возвращаем сына и дочь
    public Map<String, int[]> сrossover(int[] mother, int[] father) {
        var end = Math.floor(Math.random() * mother.length);
        var moth = mother.length;//determines length of mother
        var fath = father.length;
        int son[] = new int[fath];
        int daughter[] = new int[moth];
        for (int i = 0; i < end; i++) {
            son[i] = mother[i];
            daughter[i] = father[i];
        }
        Map<String, int[]> map = new HashMap();
        map.put("son", son);
        map.put("daughter", daughter);
        return map;
    }
    //------------------------------ФИТНЕС-ФУНКЦИЯ--------------------------------//
//проверяем есть ли пересечение прямоугольников с помощью intersection и overlap
//в prev получаем сумму прямоугольников без пересечений
    public static Rectangles fitness(Rectangles[] entity) {
        Rectangles cur = null, prev = null, red = null;
        for (int j = 0; j < entity.length; j++) {
            for (int i = j + 1; i < entity.length-1; i++) {
                red.overlap += intersection1(entity[i], entity[j]);//пересечения прямоугольников
            }
        }
        red.sum += cur.x + cur.y; //суммируем данный x и y
        red.maxx = Math.max(cur.x + cur.w, prev.maxx); //ищем максимальный x
        red.maxy = Math.max(cur.y + cur.h, prev.maxy); //ищем максимальный y
        //теперь возвращаем сумму, макс x, макс y из всех найденных
        return red;
    }

    //пересечение прямоугольников
    public static double intersection1(Rectangles r1, Rectangles r2) {
        double x, y, dx, dy;
        double dxdy;
        if (r1.x < r2.x) {
            x = r2.x;
            dx = r1.x + r1.w - r2.x;
        } else {
            x = r1.x;
            dx = r2.x + r2.w - r1.x;
        }
        if (dx < 0) dx = 0;
        if (dx > r1.w) dx = r1.w;
        if (dx > r2.w) dx = r2.w;

        if (r1.y < r2.y) {
            y = r2.y;
            dy = r1.y + r1.h - r2.y;
        } else {
            y = r1.y;
            dy = r2.y + r2.h - r1.y;
        }
        if (dy < 0) dy = 0;
        if (dy > r1.h) dy = r1.h;
        if (dy > r2.h) dy = r2.h;

        dxdy = dx * dy;

        return dxdy;//площадь
    }

    void drawRectangles(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < 15; i++) {
            Rectangles[] rect = new Rectangles[15];
            Rectangles r = new Rectangles();
            r.setX(Math.random());
            r.setY(Math.random());
            r.setH(Math.random() + .1);
            r.setW(Math.random() + .1);
            rect[i] = r;
            Rectangle2D re = new Rectangle2D.Double(rect[i].x*200, rect[i].y*200, Math.round(rect[i].w*200), Math.round(rect[i].h*200));
            g2d.draw(re);
        }
    }
    public void paint(Graphics g) {
        super.paint(g);
        drawRectangles(g);
    }

    public static void main(String[] args) throws IOException {
        //рандомно создаем 15 прямоугольников
        Main t = new Main();
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(0, 0, 15000, 15000);
        window.getContentPane().add(new Main());
        window.setVisible(true);

        for (int i = 0; i < 15; i++) {
            Rectangles[] rect = new Rectangles[15];
            Rectangles r = new Rectangles();
            r.setX(Math.random());
            r.setY(Math.random());
            r.setH(Math.random() + .1);
            r.setW(Math.random() + .1);
            rect[i] = r;
        }
    }
}





