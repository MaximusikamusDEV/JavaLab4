package Motevich.cr2.gr6.lab4;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class MainFrame extends JFrame {

    boolean fileLoaded = false;
    JFileChooser fileChooser = null;

    GraphicsDisplay display = new GraphicsDisplay();

    JFileChooser chooser = null;

    private final JCheckBoxMenuItem axisCheckItem;
    private final JCheckBoxMenuItem markersCheckItem;
    private final JCheckBoxMenuItem integralCheckItem;
    private final JCheckBoxMenuItem rotateCheckItem;

    MainFrame(){

        Toolkit kit = Toolkit.getDefaultToolkit();

        int WIDTH = 900;
        int HEIGHT = 800;
        setLocation((kit.getScreenSize().width - WIDTH) / 2, (kit.getScreenSize().height - HEIGHT) / 2 - 20);
        setSize(WIDTH, HEIGHT);

        setExtendedState(MAXIMIZED_BOTH);

        JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu file = new JMenu("Файл");
        menu.add(file);

        Action openGraphicsAtion = new AbstractAction("Выбрать данные") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chooser == null)
                {
                    chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File("."));
                }

                if(chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                {
                    openGraph(chooser.getSelectedFile());
                }
            }
        };

        file.add(openGraphicsAtion);

        JMenu graphic = new JMenu("График");
        menu.add(graphic);

        Action markersAction = new AbstractAction("Маркеры") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowMarkers(markersCheckItem.isSelected());
            }
        };

        markersCheckItem = new JCheckBoxMenuItem(markersAction);
        graphic.add(markersCheckItem);
        markersCheckItem.setSelected(true);



        Action axisAction = new AbstractAction("Оси") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowAxis(axisCheckItem.isSelected());
            }
        };

        axisCheckItem = new JCheckBoxMenuItem(axisAction);
        graphic.add(axisCheckItem);
        axisCheckItem.setSelected(true);


        Action integralAction = new AbstractAction("Интегралы") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setShowIntegral(integralCheckItem.isSelected());
            }
        };

        integralCheckItem = new JCheckBoxMenuItem(integralAction);
        graphic.add(integralCheckItem);


        Action rotateAction = new AbstractAction("Повернуть график") {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.setRotateGraph(rotateCheckItem.isSelected());
            }
        };


        rotateCheckItem = new JCheckBoxMenuItem(rotateAction);
        graphic.add(rotateCheckItem);


        JMenuItem saveToFileItem = getjMenuItem();
        file.add(saveToFileItem);


        graphic.addMenuListener(new GrMenuListener());

       getContentPane().add(display, BorderLayout.CENTER);

    }

    private JMenuItem getjMenuItem() {
        Action saveToFileAction = new AbstractAction("Сохранить данные") {
            @Override
            public void actionPerformed(ActionEvent e) {
                  if(fileChooser == null)
                  {
                      fileChooser = new JFileChooser();
                      fileChooser.setCurrentDirectory(new File("."));
                      fileChooser.setDialogTitle("Выбор директории");
                  }

                  if(fileChooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION)
                  {
                      saveToBinFile(fileChooser.getSelectedFile());
                  }
            }
        };

        return new JMenuItem(saveToFileAction);
    }

    protected void openGraph(File file) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));

            Double[][] data = new Double[in.available() / (Double.SIZE / 8) / 2][];

            int i = 0;

            while (in.available() > 0) {
                double x = in.readDouble();
                double y = in.readDouble();

              //  System.out.println(x + "  "+ y);

                data[i] = new Double[]{x, y};
                i++;
            }

            if (data.length > 0) {
                fileLoaded = true;
                display.setGraphicsData(data);
            }

            in.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(MainFrame.this,
                    "Указанный файл не найден", "Ошибка загрузки данных",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(MainFrame.this, "Ошибка чтения координат точек",
                    "Ошибка чтения", JOptionPane.WARNING_MESSAGE);

        }
    }


        class GrMenuListener implements MenuListener {

        @Override
        public void menuSelected(MenuEvent e) {
            axisCheckItem.setEnabled(fileLoaded);
            markersCheckItem.setEnabled(fileLoaded);
            integralCheckItem.setEnabled(fileLoaded);
            rotateCheckItem.setEnabled(fileLoaded);
        }

        @Override
        public void menuDeselected(MenuEvent e) {

        }

        @Override
        public void menuCanceled(MenuEvent e) {

        }
    }


    private void saveToBinFile(File outF)
    {
        try {
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outF));

            for(Double[] list: display.getGraphicsData())
            {
                out.writeDouble(list[0]);
                out.writeDouble(list[1]);
            }

            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
