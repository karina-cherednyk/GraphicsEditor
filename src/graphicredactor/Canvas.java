/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicredactor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;

/**
 *
 * @author k256
 */
public class Canvas extends JPanel{
    private class MyShape {
    public Shape shape;
    public Color color;
    public Stroke stroke;
    public MyShape(Shape sh, Color c, int st){
        shape = sh;
        color = c;
        stroke = new BasicStroke(st);
    }
    }
    
    
    public Canvas()
    {
       setHandler(bmh);
    }
 
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        for(int i=0; i<shapes.size(); i++){
        g2.setStroke(shapes.get(i).stroke);
        g2.setColor(shapes.get(i).color);
        g2.draw(shapes.get(i).shape);
        }
    }
    
    ArrayList<MyShape> shapes = new ArrayList<>();
    LinkedList<Path2D.Double> curves = new LinkedList<>();
    Point2D begin;
    Point2D end;
    BrushMouseHandler bmh = new BrushMouseHandler();
    SprayMouseHandler smh = new SprayMouseHandler();
    MouseHandler curHandler;
    Color color = Color.BLACK;
    //pencil, brush, spray, circle, rect
    BrushType type = BrushType.brush;
    int stroke = 1;
    int range = 10;
    Random r = new Random();
    
    public void setColor(Color c){
    color = c;
    }
    public void setStroke(int s){
    stroke = s;
    }
    public void setType(BrushType bt){
       type = bt;
       switch(type){
           case pencil : ; break;
           case brush : setHandler(bmh); break;
           case spray : setHandler(smh); break;
           case circle : ; break;
           case rect : ; break;
           default : break;
       }
    }
    private void setHandler(MouseHandler mh){
        if(curHandler!=null){
        removeMouseListener(curHandler);
        removeMouseMotionListener(curHandler);
        }
        curHandler = mh;
        addMouseListener(curHandler);
        addMouseMotionListener(curHandler);
    }
    private class BrushMouseHandler extends MouseHandler{
        @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            Path2D.Double current = curves.getLast();
            current.lineTo(p.getX(), p.getY());
            repaint();
        }
        @Override
        public void mousePressed(MouseEvent e) {
         Path2D.Double current = new Path2D.Double();  
         shapes.add(new MyShape(current, color, stroke));
         curves.add(current);
         Point p = e.getPoint();
         current.moveTo(p.getX(), p.getY());
        } 
    } 
    private class SprayMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            int count = r.nextInt(7)+3;
            double x,y;
            while(count>0){
            x = r.nextInt(2*range)-range + p.getX();
            y = r.nextInt(2*range)-range + p.getY();
            shapes.add(new MyShape(new Ellipse2D.Double(x,y,stroke,stroke), color, stroke));
            count--;    
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
          Point p = e.getPoint();
            int count = r.nextInt(7)+3;
            double x,y;
            while(count>0){
            x = r.nextInt(2*range*stroke)-range*stroke + p.getX();
            y = r.nextInt(2*range*stroke)-range*stroke + p.getY();
            shapes.add(new MyShape(new Ellipse2D.Double(x,y,stroke,stroke), color, stroke));
            count--;
            }
            repaint();
        }
    }
}
