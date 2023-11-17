package Motevich.cr2.gr6.lab4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class GraphicsDisplay extends JPanel {



    private final Double[] currSize = new Double[4];
    private final Double[] firstGraphicsData = new Double[4];
    private final Deque<Double[]> previousSizes = new ArrayDeque<>();

    private boolean newSize;
    private final Rectangle2D.Double choiceRect = new Rectangle2D.Double();

    private double scaleY;
    private double scaleX;

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

    private final Font axisFont;
    private final Font markerInfoFont;

    private final BasicStroke markerStroke;
    private final BasicStroke axisStroke;
    private final BasicStroke graphicStroke;

    boolean flag = false;

    private final BasicStroke sizeStroke;

    private boolean pSelected;
    private Point2D currPoint;
    private boolean pDrug;

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

        markerInfoFont = new Font("Serif", Font.BOLD, 12);

        sizeStroke = new BasicStroke(2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
                new float[] {5, 2, 5}, 0.0f);

        addMouseMotionListener(new MouseMotionListener());
        addMouseListener(new MouseStatListener());

    }

    public Double[][] getGraphicsData() {
      return graphicsData;
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
        if(!flag)
        return new Point2D.Double((x - currSize[1]) * scale, (currSize[2] - y) * scale);
        else
            return new Point2D.Double((x - currSize[1]) * scaleX, (currSize[2] - y) * scaleY);
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

        GeneralPath graph = new GeneralPath();

        int k = 0;

        for (Double[] graphicsDatum : graphicsData) {
            boolean fl = false;
            Point2D.Double point = xyToPoint(graphicsDatum[0], graphicsDatum[1]);

            String str = graphicsDatum[1].toString();
            str = str.replace(".", "");
            str = str.replace("-", "");

            char[] st = str.toCharArray();


            if (st.length == 1) {
            }
            else {
                for (int j = 1; j < st.length - 1; j++) {

                    if (st[j] <= st[j - 1]) {
                        fl = true;
                        break;
                    }
                }
            }

            if ((!rotateGraph && graphicsDatum[0] <= currSize[0] && graphicsDatum[0] >= currSize[1]
                    && graphicsDatum[1] <= currSize[2]
                    && graphicsDatum[1] >= currSize[3]) || (rotateGraph && graphicsDatum[1] <= currSize[0]
                    && graphicsDatum[1] >= currSize[1] && graphicsDatum[0] <= currSize[2]
                    && graphicsDatum[0] >= currSize[3])) {



                if (k > 0) {
                    graph.lineTo(point.getX(), point.getY());

                } else {
                    graph.moveTo(point.getX(), point.getY());
                }
                if (!fl) {
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

                }

                if (pSelected && point.equals(currPoint)) {
                    Ellipse2D.Double el = new Ellipse2D.Double();
                    Point2D.Double corn = shiftPoint(point, 7, 7);
                    el.setFrameFromCenter(point, corn);
                    Color cl = canvas.getColor();
                    Stroke newS = canvas.getStroke();
                    Font newF = canvas.getFont();
                    canvas.setStroke(oldS);
                    canvas.setColor(Color.BLACK);
                    canvas.setFont(markerInfoFont);
                    canvas.draw(el);
                    canvas.fill(el);

                    String strX = Double.toString(graphicsDatum[0]);
                    String strY = Double.toString(graphicsDatum[1]);
                    String strXY = "X: " + strX + " Y: " + strY;
                    canvas.drawString(strXY, (float) (point.getX() + 10), (float) point.getY());

                    canvas.setFont(newF);
                    canvas.setColor(cl);
                    canvas.setStroke(newS);
                }

                k++;
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
           } else {
               canvas.draw(new Line2D.Double(xyToPoint(0, maxX), xyToPoint(0, minX)));
           }
           arrow = new GeneralPath();

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
           } else {
               canvas.draw(new Line2D.Double(xyToPoint(maxY, 0), xyToPoint(minY, 0)));
           }
           arrowx = new GeneralPath();

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


                if(graph[0] <= currSize[0] && graph[0] >= currSize[1] && graph[1] <= currSize[2] && graph[1] >= currSize[3]) {
                    canvas.draw(diagl);
                    canvas.draw(vert);
                    canvas.draw(hor);
                    canvas.draw(diagr);
                }
        }
    }



    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(graphicsData == null)
            return;

    if(!flag) {
        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length - 1][0];
        maxY = minY = graphicsData[0][1];

        for (Double[] graph : graphicsData) {
            if (graph[1] < minY)
                minY = graph[1];

            if (graph[1] > maxY)
                maxY = graph[1];
        }


        addCurrSize(maxX, minX, maxY, minY);


        if (rotateGraph) {
            double tmp = maxX;
            maxX = maxY;
            maxY = tmp;

            tmp = minX;
            minX = minY;
            minY = tmp;
        }
        addCurrSize(maxX, minX, maxY, minY);

        firstGraphicsData[0] = maxX;
        firstGraphicsData[1] = minX;
        firstGraphicsData[2] = maxY;
        firstGraphicsData[3] = minY;

         scaleX = getSize().getWidth() / (currSize[0] - currSize[1]);
         scaleY = getSize().getHeight() / (currSize[2] - currSize[3]);

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

        addCurrSize(maxX, minX, maxY, minY);
    }

         scaleX = getSize().getWidth() / (currSize[0] - currSize[1]);
         scaleY = getSize().getHeight() / (currSize[2] - currSize[3]);

        //addCurrSize(maxX, minX, maxY, minY);
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
        if(newSize)
            showChoiceRect(canvas);

    }


    void showChoiceRect(Graphics2D canvas)
    {
          Stroke oldS = canvas.getStroke();
          canvas.setStroke(sizeStroke);
          canvas.draw(choiceRect);
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

        if(graphicsData[0][1] < 0.0) {

        }
        else if(graphicsData[0][1] > 0.0){
            flagIn = true;
            fl = true;
        } else {
            currList = new ArrayList<>();
            currList.add(graphicsData[0][0]);
            currList.add(graphicsData[0][1]);
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


                fl = !fl;

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


            if(integral.length() > 6)
            integral = integral.substring(0, 6);

            if(integral.equals("0.0"))
                continue;


            if(list.get(0) < 0)
                ;

            Double[] center = figCenter(list);

            double textX = center[0];
            double textY = center[1];


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
        double y2, x2;
        double totalF = 0;


        for(int i = 0; i < list.size(); i += 2)
        {
            double  currF;
            x2 = list.get(i);
            y2 = list.get(i+1);

            double h = (x2 - x1) / 100;
            Double[] kb = kAndB(x1, y1, x2, y2);

            while(true)
            {
                int n = (int) ((x2 - x1) / h);


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


            x1 = x2;
            y1 = y2;
            totalF += currF;
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
        double y = 0;

        for(int i = 0; i < list.size(); i += 2)
        {



            y += list.get(i+1);
        }

        int k = 1;
        if(list.get(list.size()-2) >= 0)
            k = -1;
        center[0] = (list.get(0) + list.get(list.size()-2)) / 2 - k;
        center[1] = y / list.size() * 2;


        return center;
    }

    private void addPrevSizes(double xMax, double xMin, double yMax, double yMin)
    {
        Double[] arr = new Double[4];
        arr[0] = xMax;
        arr[1] = xMin;
        arr[2] = yMax;
        arr[3] = yMin;
        if(previousSizes != null)
        previousSizes.addLast(arr);
        else
            previousSizes.add(arr);
    }

    private void addCurrSize(double xMax, double xMin, double yMax, double yMin)
    {
       // currSize = new Double[4];
        currSize[0] = xMax;
        currSize[1] = xMin;
        currSize[2] = yMax;
        currSize[3] = yMin;
        repaint();
    }


    private Double[] getPrevSize()
    {
        return previousSizes.pollLast();
    }

    private class MouseStatListener implements MouseListener {


        @Override
        public void mouseClicked(MouseEvent e) {

            if(e.getButton() == 3)
            {
                Double[] size = getPrevSize();
                addCurrSize(size[0], size[1], size[2], size[3]);
            }

        }

        @Override
        public void mousePressed(MouseEvent e) {

            if(e.getButton() == 1) {
                Double[] currP = pointToXY(e.getX(), e.getY());


                if(!pSelected && findPointToDrag(currP[0], currP[1]) == -1)
                {
                    //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    newSize = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    choiceRect.setFrame(e.getX(), e.getY(), 5, 5);
                  //  setCursor(Cursor.getPredefinedCursor(5));
                }
            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {

            if(e.getButton() == 1)
            {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

                if(pSelected)
                {
                    pSelected = false;
                } else
                    if(newSize) {
                        newSize =  false;
                        addPrevSizes(currSize[0], currSize[1], currSize[2], currSize[3]);

                        Double[] pp = pointToXY(choiceRect.getX(), choiceRect.getY());
                        Double[] ppp = pointToXY(choiceRect.getX()+choiceRect.getWidth(), choiceRect.getY()+choiceRect.getHeight());
                      //  System.out.println(pp[0] + "  " + pp[1] + " " + ppp[0] + " " + ppp[1]);


                        currSize[0] = 0.0;
                        addCurrSize(ppp[0], pp[0], pp[1], ppp[1]);
                        //System.out.println(currSize[0] + " " + currSize[1] + " " + currSize[2] + " " + currSize[3]);

                        flag = true;
                        repaint();
                    }
               // System.out.println("!!!!!!!!!!");
            }

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }


    }

    private class MouseMotionListener implements java.awt.event.MouseMotionListener{

        @Override
        public void mouseDragged(MouseEvent e) {
            if(pSelected) {
                pDrug = true;
                //pSelected = true;
                double currCurY = e.getY();
                double currCurX = e.getX();

                int i = findPointToDrag(currCurX, currCurY);
                Double[] xyMouse = pointToXY(currCurX, currCurY);
                if(i != -1) {
                    graphicsData[i][1] = xyMouse[1];
                    currPoint = xyToPoint(graphicsData[i][0], graphicsData[i][1]);
                    repaint();
                }

                repaint();
            }
            else {

                if(newSize)
                {
                    double width = e.getX() - choiceRect.getX();
                    double height = e.getY() - choiceRect.getY();

                    if(width < 5.0)
                        width = 5.0;

                    if(height < 5.0)
                        height = 5.0;



                    choiceRect.setFrame(choiceRect.getX(), choiceRect.getY(), width, height);
                    repaint();
                }

                pDrug = false;

                repaint();
            }

        }

        public void mouseMoved(MouseEvent e) {
            double x = e.getX();
            double y = e.getY();
            currPoint = findPoint(x, y);

            if(pSelected) {
                setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
            } else
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            GraphicsDisplay.this.repaint();
        }



    }

    private Point2D findPoint(double x, double y)
    {
        if(graphicsData == null) {
            pSelected = false;
            return null;
        }

        //pSelected = true;

        for(Double[] list: graphicsData)
        {
            Point2D p = xyToPoint(list[0], list[1]);

            if(Math.sqrt((p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y)) < 10)
            {
                pSelected = true;
                repaint();
                return p;
            }
            else
            if(!pDrug) {
            }

            pSelected = false;
        }

        return null;
    }

    private int findPointToDrag(double x, double y)
    {
        if(graphicsData == null) {
            pSelected = false;
            return -1;
        }

        int i = 0;

        for(Double[] list: graphicsData)
        {
            Point2D p = xyToPoint(list[0], list[1]);

            if(Math.sqrt((p.getX() - x) * (p.getX() - x) + (p.getY() - y) * (p.getY() - y)) < 10)
            {
                pSelected = true;
                return i;
            }
            else
                pSelected = false;
            i++;
        }

        return -1;
    }

    private Double[] pointToXY(double x, double y)
    {
        Double[] newdoub = new Double[2];
        newdoub[0] = currSize[1] + x / scaleX;
        newdoub[1] =  currSize[2] - y / scaleY;
        return newdoub;
    }

}

