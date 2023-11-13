package Motevich.cr2.gr6.lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.awt.image.renderable.RenderContext;
import java.util.ArrayList;
import java.util.Vector;

public class GraphicsDisplay extends JPanel {


    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showIntegral;
    private boolean rotateGraph;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private Font axisFont;

    private BasicStroke markerStroke;
    private BasicStroke axisStroke;
    private BasicStroke graphicStroke;

    public GraphicsDisplay()
    {
        setBackground(Color.WHITE);

        graphicStroke = new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
                new float[] {6, 4, 1, 4, 1, 4, 3, 4, 3, 4}, 0.0f);

        axisStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                null, 0.0f);

        markerStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);

    }

    public void setGraphicsData(Double[][] graphicsData)
    {
        this.graphicsData = graphicsData;

        repaint();
    }

    public void setRotateGraph(boolean rotateGraph)
    {
        this.rotateGraph = rotateGraph;

        repaint();
    }

    public void setShowIntegral(boolean integral)
    {
        this.showIntegral = integral;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers)
    {
        this.showMarkers = showMarkers;
        repaint();
    }

    public void setShowAxis(boolean showAxis)
    {
        this.showAxis = showAxis;
        repaint();
    }

    protected Point2D.Double xyToPoint(double x, double y)
    {
        return new Point2D.Double((x - minX) * scale, (maxY - y) * scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double delX, double delY)
    {
             Point2D.Double dest = new Point2D.Double();
             dest.setLocation(src.getX() + delX, src.getY() + delY);

             return dest;
    }

    protected void paintGraphic(Graphics2D canvas, Stroke oldS)
    {
        canvas.setStroke(graphicStroke);
        canvas.setColor(Color.BLUE);

        //Double lastP = 0.0;

        GeneralPath graph = new GeneralPath();

        for(int i = 0; i < graphicsData.length; i++)
        {
            boolean fl = false;
            Point2D.Double point = xyToPoint(graphicsData[i][0], graphicsData[i][1]);

            String str = graphicsData[i][1].toString();
            str = str.replace(".", "");
            str = str.replace("-", "");

            char[] st = str.toCharArray();


            if(st.length == 1)
                fl = false;
            else {
                for (int j = 1; j < st.length-1; j++) {

                    if (st[j] <= st[j-1]) {
                        fl = true;
                        break;
                    }
                }
            }


            if(i > 0)
            {
                graph.lineTo(point.getX(), point.getY());
            } else
                graph.moveTo(point.getX(), point.getY());

            if(fl == false)
            {
                Ellipse2D.Double el = new Ellipse2D.Double();
                Point2D.Double corn = shiftPoint(point, 5, 5);
                el.setFrameFromCenter(point, corn);
                Color cl = canvas.getColor();
                Stroke newS = canvas.getStroke();
                canvas.setStroke(oldS);
                canvas.setColor(Color.YELLOW);
                canvas.draw(el);
                canvas.fill(el);
                canvas.setColor(cl);
                canvas.setStroke(newS);
              //  System.out.println("+");
            }

        }

        canvas.draw(graph);

    }

    protected void paintAxis(Graphics2D canvas)
    {
       canvas.setStroke(axisStroke);
       canvas.setColor(Color.BLACK);
       canvas.setPaint(Color.GREEN);
       canvas.setFont(axisFont);

       FontRenderContext context = canvas.getFontRenderContext();

       if(minX <= 0.0 && maxX >= 0.0)
       {
           GeneralPath arrow;
           if(!rotateGraph) {
               canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
              arrow = new GeneralPath();
           } else {
               canvas.draw(new Line2D.Double(xyToPoint(0, maxX), xyToPoint(0, minX)));
              arrow = new GeneralPath();
           }

           Point2D.Double lineEnd;

           if(!rotateGraph)
               lineEnd = xyToPoint(0, maxY);
           else
               lineEnd = xyToPoint(0, maxX);


           arrow.moveTo(lineEnd.getX(), lineEnd.getY());
           arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
           arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());
           arrow.closePath();

           canvas.draw(arrow);
           canvas.fill(arrow);

           Rectangle2D bounds = axisFont.getStringBounds("y", context);

           Point2D yPos;

           if(!rotateGraph)
               yPos = xyToPoint(0, maxY);
           else
               yPos = xyToPoint(0, maxX);

           canvas.drawString("y", (float)(yPos.getX() + 10), (float)(yPos.getY() - bounds.getY()));

       }

       if(minY <= 0.0 && maxY >= 0.0)
       {
           GeneralPath arrowx;
           if(!rotateGraph) {
               canvas.draw(new Line2D.Double(xyToPoint(maxX, 0), xyToPoint(minX, 0)));
                arrowx = new GeneralPath();
           } else {
               canvas.draw(new Line2D.Double(xyToPoint(maxY, 0), xyToPoint(minY, 0)));
                arrowx = new GeneralPath();
           }

           Point2D.Double lineEnd;

           if(!rotateGraph)
               lineEnd = xyToPoint(maxX, 0);
           else
               lineEnd = xyToPoint(maxY, 0);

           arrowx.moveTo(lineEnd.getX(), lineEnd.getY());
           arrowx.lineTo(arrowx.getCurrentPoint().getX()-20, arrowx.getCurrentPoint().getY()-5);
           arrowx.lineTo(arrowx.getCurrentPoint().getX(), arrowx.getCurrentPoint().getY()+10);
           arrowx.closePath();

           canvas.draw(arrowx);
           canvas.fill(arrowx);

           Rectangle2D bounds = axisFont.getStringBounds("x", context);

           Point2D xPos;
           if(!rotateGraph)
               xPos = xyToPoint(maxX, 0);
           else
               xPos = xyToPoint(maxY, 0);

           canvas.drawString("x", (float)(xPos.getX() - bounds.getWidth() - 10), (float)(xPos.getY() + bounds.getY()));
       }

    }

    protected void paintMarkers(Graphics2D canvas)
    {
        canvas.setStroke(markerStroke);
        canvas.setColor(Color.RED);
        canvas.setPaint(Color.MAGENTA);

        for(Double[] graph: graphicsData)
        {
            Point2D.Double center = xyToPoint(graph[0], graph[1]);

            Point2D.Double diagl1 = shiftPoint(center, 5, 5);
            Point2D.Double diagl0 = shiftPoint(center, -5, -5);
            Line2D.Double diagl;
            diagl = new Line2D.Double(diagl0, diagl1);

            Point2D.Double diagr1 = shiftPoint(center, -5, 5);
            Point2D.Double diagr0 = shiftPoint(center, 5, -5);
            Line2D.Double diagr;
            diagr = new Line2D.Double(diagr0, diagr1);

            Point2D.Double vert1 = shiftPoint(center, 0, 5);
            Point2D.Double vert0 = shiftPoint(center, 0, -5);
            Line2D.Double vert;
            vert = new Line2D.Double(vert0, vert1);

            Point2D.Double hor1 = shiftPoint(center, 5, 0);
            Point2D.Double hor0 = shiftPoint(center, -5, 0);
            Line2D.Double hor;
            hor = new Line2D.Double(hor0, hor1);

            canvas.draw(diagl);
            canvas.draw(vert);
            canvas.draw(hor);
            canvas.draw(diagr);
        }
    }



    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(graphicsData == null)
            return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        maxY = minY = graphicsData[0][1];

        for(Double[] graph: graphicsData)
        {
            if(graph[1] < minY)
                minY = graph[1];

            if(graph[1] > maxY)
                maxY = graph[1];
        }

        if(rotateGraph) {
            double tmp = maxX;
            maxX = maxY;
            maxY = tmp;

            tmp = minX;
            minX = minY;
            minY = tmp;
        }


            double scaleX = getSize().getWidth()  / (maxX - minX);
            double scaleY = getSize().getHeight() / (maxY - minY);

            scale = Math.min(scaleX, scaleY);

            if (scale == scaleX) {
                double incY = (getSize().getHeight() / scale - (maxY - minY)) / 2;
                maxY += incY;
                minY -= incY;
            }

            if (scale == scaleY) {
                double incX = (getSize().getWidth() / scale - (maxX - minX)) / 2;
                maxX += incX;
                minX -= incX;
            }

        Graphics2D canvas = (Graphics2D) g;

        Stroke oldS = canvas.getStroke();
        Color oldC = canvas.getColor();
        Font oldF = canvas.getFont();
        Paint oldP = canvas.getPaint();





        if(rotateGraph)
            rotatingGraph(canvas);

        if(showIntegral)
            calcAndFormIntegral(canvas);
        if(showAxis) paintAxis(canvas);
        paintGraphic(canvas, oldS);
        if(showMarkers) paintMarkers(canvas);



        canvas.setFont(oldF);
        canvas.setPaint(oldP);
        canvas.setColor(oldC);
        canvas.setStroke(oldS);
    }

    void rotatingGraph(Graphics2D canvas)
    {
        Point2D p = xyToPoint(0, 0);
        canvas.rotate(Math.toRadians(-90), p.getX(), p.getY());
        canvas.translate(0,getSize().height/2);
        repaint();
    }



    private Double newPoint(double prX, double prY, double currX, double currY)
    {
        double newX;

        double tmpX, tmpY;
        double k;                                                   // y = kx + b
        double b;

        tmpX = prX - currX;
        tmpY = prY - currY;
        k = tmpY / tmpX;
        b = currY - currX * k;
        newX = -b / k;

        return newX;
    }

    private void calcAndFormIntegral(Graphics2D canvas)
    {
        boolean flagIn = false;
        boolean flagOut = false;
        boolean fl = false;
        int mem = 0;
        ArrayList<ArrayList<Double>> listOfPoints = new ArrayList<>();
        ArrayList<Double> currList = null;

        if(graphicsData[0][1] < 0.0) {flagIn = false;
            fl = false;
        }
        else if(graphicsData[0][1] > 0.0){
            flagIn = true;
            fl = true;
        } else if(graphicsData[0][1] == 0.0)
        {
            currList = new ArrayList<>();
            currList.add(graphicsData[0][0]);
            currList.add(graphicsData[0][1]);
            flagIn = false;
            fl = false;
            mem = 0;
            flagOut = true;
        }

        for(int i = 1; i < graphicsData.length; i++)
        {
            if(((graphicsData[i][1] >= 0 && !flagIn) || (graphicsData[i][1] <= 0 && flagIn)) && (!flagOut)) {
                mem = i - 1;
                currList = new ArrayList<>();

                if(graphicsData[i][1] != 0.0) {
                    currList.add(newPoint(graphicsData[mem][0], graphicsData[mem][1], graphicsData[i][0], graphicsData[i][1]));
                    currList.add(0.0);
                } else {
                    currList.add(graphicsData[i][0]);
                    currList.add(graphicsData[i][1]);
                }

                flagOut = true;
            }

            if( (((graphicsData[i][1] >= 0.0 && flagIn) || (graphicsData[i][1] <= 0.0 && !flagIn)) && flagOut && !fl)
                    ||
                    (((graphicsData[i][1] >= 0.0 && !flagIn) || (graphicsData[i][1] <= 0.0 && flagIn)) && flagOut && fl)
            )
            {
                for(int j = mem + 1; j < i; j++) {
                    currList.add(graphicsData[j][0]);
                    currList.add(graphicsData[j][1]);
                }

                if(graphicsData[i][1] != 0.0) {
                    currList.add(newPoint(graphicsData[i-1][0], graphicsData[i-1][1], graphicsData[i][0], graphicsData[i][1]));
                    currList.add(0.0);
                } else {
                    currList.add(graphicsData[i][0]);
                    currList.add(graphicsData[i][1]);
                }


                if(fl)
                    fl = false;
                else
                    fl = true;

                listOfPoints.add(currList);
                currList = new ArrayList<>();

                mem = i-1;

                if(i != graphicsData.length+1)
                {
                    if(graphicsData[i][1] != 0.0) {
                        currList.add(newPoint(graphicsData[mem][0], graphicsData[mem][1], graphicsData[i][0], graphicsData[i][1]));
                        currList.add(0.0);
                    } else {
                        currList.add(graphicsData[i][0]);
                        currList.add(graphicsData[i][1]);
                    }

                }
            }

        }

        for(ArrayList<Double> list: listOfPoints)
        {

            String integral = Double.toString(calcIntegral(list));

            GeneralPath fig = new GeneralPath();
            Point2D.Double point = xyToPoint(list.get(0), list.get(1));
            fig.moveTo(point.getX(), point.getY());

            for(int i = 2; i < list.size(); i += 2)
            {
                      Point2D.Double pointEnd = xyToPoint(list.get(i), list.get(i+1));
                      fig.lineTo(pointEnd.getX(), pointEnd.getY());
            }

            fig.lineTo(point.getX(), point.getY());



           // System.out.println(bd.getX() + " " + bd.getY() + " " + bounds.getX() + " " + bounds.getY());

            if(integral.length() > 6)
            integral = integral.substring(0, 6);

            if(integral.equals("0.0"))
                continue;


            int k = 1;
            int z = 1;

            if(list.get(0) < 0)
                k = -1;

            Rectangle2D ptFig = fig.getBounds2D();

            Double[] center = figCenter(list);

            double textX = center[0];
            double textY = center[1];
           // Point2D ptFig = fig.ge;

            Point2D.Double pointText = xyToPoint(textX,
                    textY);



            int flag = 0;
            int moves = 1;

            while(!fig.contains(pointText))
            {
                int f = 1;
                if(list.get(list.size()-1) > 0.0)
                    f = -1;

                if(flag == 0)
                pointText = xyToPoint(textX, textY - f * moves * 0.5);

                if(flag == 1)
                    pointText = xyToPoint(textX - moves * 0.5, textY);

                if(flag == 2)
                    pointText = xyToPoint(textX + moves * 0.5, textY);

                moves++;

                if(moves > 100000) {
                    flag++;
                    moves = 0;
                    pointText = xyToPoint(textX, textY);
                }
            }


            canvas.setColor(Color.GREEN);
            canvas.setPaint(Color.RED);


            canvas.draw(fig);
            canvas.fill(fig);

            fig.closePath();

            canvas.setPaint(Color.BLACK);

            canvas.setStroke(axisStroke);

                canvas.drawString(integral, (float) (pointText.getX()), (float) (pointText.getY()));




        }
    }

    double calcIntegral(ArrayList<Double> list)
    {
        double y1 = 0.0, x1 = 0.0;
        double y2 = 0.0, x2 = 0.0;
        double totalF = 0;
        boolean fl = false;


        for(int i = 0; i < list.size(); i += 2)
        {
            double prevF = 0.0;
            double  currF = 0.0;
            if(i % 4 != 0 && !fl)
            {
                x1 = list.get(i);
                y1 = list.get(i+1);
            } else
            {
                prevF = currF;

                x2 = list.get(i);
                y2 = list.get(i+1);

                double eps = 1e-03;
                double h = (x2 - x1) / 100;
                Double[] kb = kAndB(x1, y1, x2, y2);

                while(true)
                {
                    int n = (int) ((x2 - x1) / h);

                    prevF = currF;


                    currF = (kb[0] * (x1 + h * 1) + kb[1]);
                    currF += (kb[0] * (x1 + h * n) + kb[1]);

                    for(int j = 1; j < n - 1; j++)
                    {
                        currF += 2 * (kb[0] * (x1 + h * j) + kb[1]);
                        currF *= 1000;
                        Math.ceil(currF);
                        currF /= 1000;
                    }

                    currF /= 2;
                    currF *= h;

                        break;
                }



                fl = true;
                 x1 = x2;
                 y1 = y2;
                 totalF += currF;
               // System.out.println("  - "+ currF + " " + totalF);
            }
        }



        return Math.abs(totalF);

    }

    private Double[] kAndB(double prX, double prY, double currX, double currY)
    {
        double tmpX, tmpY;
        double k;                                                   // y = kx + b
        double b;

        if(prX == currX)
            tmpX = prX;
        else
            tmpX = prX - currX;

        if(prY == currY)
            tmpY = prY;
        else
            tmpY = prY - currY;


        k = tmpY / tmpX;
        b = currY - currX * k;

        Double[] kAndB = new Double[2];
        kAndB[0] = k;
        kAndB[1] = b;

        return kAndB;
    }

    private Double[] figCenter(ArrayList<Double> list)
    {
        Double[] center = new Double[2];
        double x = 0;
        double y = 0;
        boolean fl = false;

        for(int i = 0; i < list.size(); i += 2)
        {



            y += list.get(i+1);
        }

        //center[0] = x / list.size() * 2;
      //  System.out.println(center[0]);
        int k = 1;
        if(list.get(list.size()-2) >= 0)
            k = -1;
        center[0] = (list.get(0) + list.get(list.size()-2)) / 2 - k * 1;
        center[1] = y / list.size() * 2;



        //center[1] = y / list.size() * 2;
       // System.out.println(center[1]);

        return center;
    }



}

