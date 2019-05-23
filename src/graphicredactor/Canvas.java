/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphicredactor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.JPanel;

interface Drawable{ public void draw(Graphics2D g2);}
/**
 *
 * @author k256
 */
public class Canvas extends JPanel {
    private LinkedList<Drawable>[] saves = new LinkedList[10];
    int current;
    
    public void checkPrevious(){
    if(current-1<0) return;
    shapes = saves[current--];
    repaint();
    }
    public void checkNext(){
    if(current+1>=saves.length || saves[current+1]==null) return;
    shapes = saves[current++];
    repaint();
    }
       public Canvas()
    {
       setHandler(bmh);
       addMouseListener(new MouseListener(){
           @Override
           public void mouseReleased(MouseEvent e) {   
               LinkedList<Drawable> curShapes = new LinkedList<>();
               for(Drawable d: shapes) curShapes.add(d);
               if(current+1==saves.length){
               for(int i=1; i<saves.length; i++) saves[i-1] = saves[i];
               saves[saves.length-1] = curShapes;
               }
               else if(saves[current+1]==null)saves[current++] = curShapes;
               else {
               saves[current++] = curShapes;
               for(int i=current+1;i<saves.length; i++) saves[i] = null;
               }
           }
           @Override
           public void mouseEntered(MouseEvent e) {}
           @Override
           public void mouseExited(MouseEvent e) {}
           @Override
           public void mousePressed(MouseEvent e) {}
           @Override
           public void mouseClicked(MouseEvent e) {}
       });
    }
    
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
    public MyShape(Shape sh, boolean f,Color c){
        shape = sh;
        fill = f;
        MScolor = c;
    }
     @Override
    public void draw(Graphics2D g2) {
       g2.setStroke(MSstroke);
       g2.setColor(MScolor);
       if(fill)g2.fill(shape);
       g2.draw(shape);
    }
    }
    private class MySelect implements Drawable{
    public Rectangle2D area;
    private Stroke MSstroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_BEVEL, 1, new float[]{5, 5}, 0);
    private Color MScolor = Color.BLUE; 
    private double x,y;
    public MySelect(Rectangle2D rect){
    area = rect;
    }
        @Override
        public void draw(Graphics2D g2) {
             g2.setStroke(MSstroke);
             g2.setColor(MScolor);
             g2.draw(area);
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
    private class MyImage implements Drawable{
    public BufferedImage MIimage = image;
    public int width = image.getWidth();
    public int height = image.getHeight();
    public int x,y;
        public MyImage(int x, int y){
        this.x = x;
        this.y = y;
         }
        @Override
        public void draw(Graphics2D g2) {
           g2.drawImage(MIimage, x, y, width, height, null); 
        }
    
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
    ImageMouseHandler imh = new ImageMouseHandler();
    ImageCreateHandler ich = new ImageCreateHandler();
    CutCopyMouseHandler ccmh = new CutCopyMouseHandler();
    PasteMouseHandler pmh = new PasteMouseHandler();
    FillMouseHandler fmh = new FillMouseHandler();
    TransformMouseHandler trmh = new TransformMouseHandler();
    PickColorHandler pch = new PickColorHandler();
    EraseMouseHandler emh = new EraseMouseHandler();
    MouseHandler curHandler;
    Color color = Color.BLACK;
    ToolType type = ToolType.brush;
    int stroke = 2;
    int range = 5;
    Random r = new Random();
    double beginX;
    double beginY;
    boolean fill;
    String text="";
    Font font =  new Font("Times New Roman",Font.PLAIN,10);
    BufferedImage image;
    JPanel col1;
    JPanel col2;
    public void setPanels(JPanel c1,JPanel c2){
    col1=c1; col2=c2;
    }
    //resize or move
    private boolean resize = true;
    
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
    public void setImage(BufferedImage myPicture) {
        image = myPicture;
    }
    public void setImageTransform(boolean res){
    resize = res;
    }
    public void setCut(boolean c){
    cut = c;
    }
    public void clear(){
    shapes.clear();
    repaint();
    }
    public void fill(){
    shapes.clear();
    setBackground(color);
    repaint();
    }
    
    public void setType(ToolType bt){
       type = bt;
       switch(type){
           case line : setHandler(lmh); break;
           case brush : setHandler(bmh); break;
           case spray : setHandler(smh); break;
           case circle : setHandler(cmh); break;
           case rect : setHandler(rmh); break;
           case text : setHandler(tmh); break;
           case imageCreate : setHandler(ich); break;
           case imageTransform : setHandler(imh); break;
           case cutCopy : setHandler(ccmh); break;
           case paste : setHandler(pmh); break;
           case fill: setHandler(fmh); break;
           case transform : setHandler(trmh); break;    
           case pickColor : setHandler(pch); break;    
           case erase: setHandler(emh); break;    
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
  
    private class ImageMouseHandler extends MouseHandler{
    private int pOX, pOY;
    private int iOX, iOY;
    private MyImage currentImage;
        @Override
        public void mousePressed(MouseEvent e) {
            Point p = e.getPoint();
            Iterator<Drawable> i = shapes.descendingIterator();
            Drawable shape;
            while(i.hasNext()){
            shape = i.next();
            if(shape instanceof MyImage){
            MyImage img = (MyImage)shape;    
            int xBegin = img.x;
            int xEnd = img.x+img.width;
            int yBegin = img.y;
            int yEnd = img.y+img.height;
            int x = p.x; int y = p.y;
            if(x>=xBegin&&x<=xEnd&&y>=yBegin&&y<=yEnd){
                currentImage=img;
                pOX = x; pOY=y;
                iOX = img.x; iOY=img.y;
                break;
            }
            } 
            }
            }
        @Override
        public void mouseDragged(MouseEvent e) {
         Point p = e.getPoint();   
         if(currentImage==null) return;
         MyImage image = currentImage;
         if(resize) {
            int x = image.x;
            int y = image.y;
            if(x<=0 || y<=0)return;
            int newWidth = p.x - x;
            int newHeight = p.y - y;
            if(newWidth<=0 || newHeight<=0) return;
            image.width = newWidth;
            image.height = newHeight;
         }
         else {
            
            image.x = iOX+(p.x-pOX);
            image.y = iOY+(p.y-pOY);
         }
         repaint();
        }
        @Override
        public void mouseReleased(MouseEvent e){ 
        currentImage=null;
        }
    }
    private class ImageCreateHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
          Point p = e.getPoint();
            shapes.add(new MyImage(p.x,p.y));
            repaint();
        }
        public void mouseDragged(MouseEvent e) {
        }
    }
    private Rectangle2D selectedArea;
    private boolean cut;
    private BufferedImage beforeCut;
    private class CutCopyMouseHandler extends MouseHandler{
        private double beginX, beginY;
        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();
           beginX = p.getX();
           beginY = p.getY();
           shapes.add(new MySelect(new Rectangle2D.Double(beginX, beginY, 1, 1)));
        }

        @Override
        public void mouseDragged(MouseEvent e) {
           Point p = e.getPoint();
           MySelect ms = (MySelect)shapes.getLast(); 
           Rectangle2D.Double rect;
           double width = p.getX()-beginX;
           double height = p.getY()-beginY;
           if(width<=0 && height<=0) {
               rect = new Rectangle2D.Double(beginX+width, beginY+height, -width, -height);
           }
           else if(width<=0 ) rect = new Rectangle2D.Double(beginX+width, beginY, -width, height);
           else if(height<=0) rect = new Rectangle2D.Double(beginX, beginY+height, width, -height);
           else rect = new Rectangle2D.Double(beginX, beginY, width, height);
           ms.area = rect;
           repaint(); 
        }
        @Override
        public void mouseReleased(MouseEvent e){ 
        MySelect ms = (MySelect)shapes.removeLast();
        selectedArea = ms.area;
        beforeCut = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = beforeCut.createGraphics();
          paint(graphics2D);
          graphics2D.dispose();
        if(cut) shapes.add(new MyShape(selectedArea,true,Canvas.this.getBackground()));  
        repaint();
        }
    }
    private class PasteMouseHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
          if(selectedArea == null) return; 
          Point p = e.getPoint();
          image = beforeCut.getSubimage((int)selectedArea.getX(), (int)selectedArea.getY(), (int)selectedArea.getWidth(), (int)selectedArea.getHeight());
          shapes.add(new MyImage(p.x,p.y));
          repaint();
        }
        @Override
        public void mouseDragged(MouseEvent e) {
        }
        public void mouseReleased(MouseEvent e){ 
            selectedArea = null;
        }
    }
    private class FillMouseHandler extends MouseHandler {
        BufferedImage newLook;
        int oldColor;
        int newColor;
        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();
          newLook = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = newLook.createGraphics();
          paint(graphics2D);
          graphics2D.dispose();
          
          oldColor = newLook.getRGB(p.x, p.y);
          newColor = color.getRGB();
          flood(p.x,p.y);
          shapes.clear();
          image = newLook;
          shapes.add(new MyImage(0,0));
          repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }
        private void flood(int x, int y){
           if(x<0 || x>=Canvas.this.getWidth() || y<0 || y>=Canvas.this.getHeight()) return;
           if(newLook.getRGB(x, y) != oldColor) return;
            newLook.setRGB(x, y, newColor);
            flood(x,y-1); flood(x,y+1);flood(x-1,y); flood(x+1,y); 
        }
    }
    private class TransformMouseHandler extends MouseHandler {
    private int pOX, pOY;
    private BufferedImage myImage;
    private MyImage trImg;
    private int beginX, beginY;
    private boolean first = true;
    private MySelect ms;
        @Override
        public void mousePressed(MouseEvent e) {
           Point p = e.getPoint();  
           if(first) {
               beginX = p.x;
               beginY = p.y;
               ms = new MySelect(new Rectangle2D.Double(beginX, beginY, 1, 1));
               shapes.add(ms);
               return;
           }
           pOX = p.x;
           pOY = p.y;
            }
        @Override
        public void mouseDragged(MouseEvent e) {
               Point p = e.getPoint(); 
            if(first){
           Rectangle2D.Double rect;
           double width = p.getX()-beginX;
           double height = p.getY()-beginY;
           if(width<=0 && height<=0) rect = new Rectangle2D.Double(beginX+width, beginY+height, -width, -height);
           else if(width<=0 ) rect = new Rectangle2D.Double(beginX+width, beginY, -width, height);
           else if(height<=0) rect = new Rectangle2D.Double(beginX, beginY+height, width, -height);
           else rect = new Rectangle2D.Double(beginX, beginY, width, height);
           ms.area = rect;
           repaint();  
           return;
            }

         if(resize) {
            int x = trImg.x;
            int y = trImg.y;
            if(x<=0 || y<=0)return;
            int newWidth = p.x - x;
            int newHeight = p.y - y;
            if(newWidth<=0 || newHeight<=0) return;
            trImg.width = newWidth;
            trImg.height = newHeight;
         }
         else {   
            trImg.x = beginX+(p.x-pOX);
            trImg.y = beginY+(p.y-pOY);
         }
         repaint();
        }
        @Override
        public void mouseReleased(MouseEvent e){ 
        if(first){
            shapes.remove(ms);
            Rectangle2D rect = ms.area;
            myImage  = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = myImage.createGraphics();
          paint(graphics2D);
          graphics2D.dispose();
          myImage = myImage.getSubimage((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
          first = false;
          image = myImage;
          trImg = new MyImage(beginX,beginY);
          shapes.add(trImg);
          repaint();
          return;
        }    
        first = true;
        repaint();
        }
    }
    private class PickColorHandler extends MouseHandler{

        @Override
        public void mousePressed(MouseEvent e) {
          Point p = e.getPoint();  
          BufferedImage image  = new BufferedImage(getWidth(),getHeight(), BufferedImage.TYPE_INT_RGB);
          Graphics2D graphics2D = image.createGraphics();
          paint(graphics2D);
          graphics2D.dispose();
          
          color = Color.decode(""+image.getRGB(p.x, p.y));
          col1.setBackground(color);
          col2.setBackground(color);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }
    }
    private class EraseMouseHandler extends MouseHandler{
     MyShape eraser;
     @Override
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            Path2D.Double current = (Path2D.Double)eraser.shape;
            current.lineTo(p.getX(), p.getY());
            repaint();
        }
        @Override
        public void mousePressed(MouseEvent e) {
         Path2D.Double current = new Path2D.Double();  
         eraser = new MyShape(current, fill,Canvas.this.getBackground());
         shapes.add(eraser);
         Point p = e.getPoint();
         current.moveTo(p.getX(), p.getY());
        } 
    }
}
