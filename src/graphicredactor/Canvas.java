/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicredactor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JPanel;

interface Drawable{ public void draw(Graphics2D g2);}
/**
 *
 * @author k256
 */
public class Canvas extends JPanel {

  

 
    private class MyShape implements Drawable{
    public Shape shape;
    public Color MScolor = color;
    public Stroke MSstroke = new BasicStroke(stroke);
    public boolean fill = false;
    public MyShape(Shape sh){
        shape = sh;
    }
     public MyShape(Shape sh, boolean f){
        shape = sh;
        fill = f;
    }
     @Override
    public void draw(Graphics2D g2) {
       g2.setStroke(MSstroke);
       g2.setColor(MScolor);
       if(fill)g2.fill(shape);
       g2.draw(shape);
    }
    }
    private class MyString implements Drawable{
    public String MSstring = text;
    public Color MScolor = color;
    public Font MSfont = font;
    int x,y;
    public MyString(int x, int y){
    this.x = x;
    this.y = y;
    }

        @Override
        public void draw(Graphics2D g2) {
            g2.setFont(MSfont);
            g2.setColor(MScolor);
            g2.drawString(MSstring, x, y);
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
        for (Drawable shape : shapes) shape.draw(g2);
    }
    
    LinkedList<Drawable> shapes = new LinkedList<>();
    LinkedList<Path2D.Double> curves = new LinkedList<>();
    BrushMouseHandler bmh = new BrushMouseHandler();
    SprayMouseHandler smh = new SprayMouseHandler();
    CircleMouseHandler cmh = new CircleMouseHandler();
    RectMouseHandler rmh = new RectMouseHandler();
    LineMouseHandler lmh = new LineMouseHandler();
    TextMouseHandler tmh = new TextMouseHandler();
    MouseHandler curHandler;
    Color color = Color.BLACK;
    //pencil, brush, spray, circle, rect
    BrushType type = BrushType.brush;
    int stroke = 1;
    int range = 5;
    Random r = new Random();
    double beginX;
    double beginY;
    boolean fill;
    String text;
    Font font =  new Font("Times New Roman",Font.PLAIN,10);
    
    public void setColor(Color c){
    color = c;
    }
    public void setStroke(int s){
    stroke = s;
    }
    public void setFill(boolean f){
    fill = f;
    }
    public void setText(String str){
    text = str;
    }
    public void setTextFont(Font f){
    font = f;
    } 
    public void setType(BrushType bt){
       type = bt;
       switch(type){
           case line : setHandler(lmh); break;
           case brush : setHandler(bmh); break;
           case spray : setHandler(smh); break;
           case circle : setHandler(cmh); break;
           case rect : setHandler(rmh); break;
           case text : setHandler(tmh); break;
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
         shapes.add(new MyShape(current, fill));
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
            shapes.add(new MyShape(new Ellipse2D.Double(x,y,stroke,stroke)));
            count--;    
            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
          Point p = e.getPoint();
            int count = r.nextInt(17)+3;
            double x,y;
            while(count>0){
            x = r.nextInt(2*range*stroke)-range*stroke + p.getX();
            y = r.nextInt(2*range*stroke)-range*stroke + p.getY();
            shapes.add(new MyShape(new Ellipse2D.Double(x,y,stroke,stroke)));
            count--;
            }
            repaint();
        }
    }
    private class CircleMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();
           Ellipse2D.Double el = new Ellipse2D.Double(p.getX(), p.getY(), 1, 1);
           beginX = p.getX();
           beginY = p.getY();
           shapes.add(new MyShape(el,fill));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
           Point p = e.getPoint();
           MyShape ms = (MyShape)shapes.getLast(); 
           Ellipse2D.Double el;
           double width = p.getX()-beginX;
           double height = p.getY()-beginY;
           if(width<=0 && height<=0) {
               el = new Ellipse2D.Double(beginX+width, beginY+height, -width, -height);
           }
           else if(width<=0 ) el = new Ellipse2D.Double(beginX+width, beginY, -width, height);
           else if(height<=0) el = new Ellipse2D.Double(beginX, beginY+height, width, -height);
           else el = new Ellipse2D.Double(beginX, beginY, width, height);
           ms.shape = el;
           repaint();
        }
    }
    private class RectMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();
           Rectangle2D.Double rect = new  Rectangle2D.Double(p.getX(), p.getY(), 1, 1);
           beginX = p.getX();
           beginY = p.getY();
           shapes.add(new MyShape(rect,fill));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
           Point p = e.getPoint();
           MyShape ms = (MyShape)shapes.getLast(); 
           Rectangle2D.Double rect;
           double width = p.getX()-beginX;
           double height = p.getY()-beginY;
           if(width<=0 && height<=0) {
               rect = new Rectangle2D.Double(beginX+width, beginY+height, -width, -height);
           }
           else if(width<=0 ) rect = new Rectangle2D.Double(beginX+width, beginY, -width, height);
           else if(height<=0) rect = new Rectangle2D.Double(beginX, beginY+height, width, -height);
           else rect = new Rectangle2D.Double(beginX, beginY, width, height);
           ms.shape = rect;
           repaint(); 
        }
    }
    private class LineMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();
           Line2D.Double line = new  Line2D.Double(p.getX(), p.getY(), p.getX(), p.getY());
           shapes.add(new MyShape(line));
           beginX = p.getX();
           beginY = p.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
           Line2D.Double line = (Line2D.Double)((MyShape)shapes.getLast()).shape; 
           Point p = e.getPoint();
           line.setLine(beginX, beginY, p.getX(), p.getY());
           repaint();
        }
        
    } 
    private class TextMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        shapes.add(new MyString(p.x,p.y));
        repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }
    }
}
