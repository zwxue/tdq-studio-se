// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.MenuItem;
import java.awt.Paint;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.talend.dq.indicators.graph.EdgeDisplayPredicate;
import org.talend.dq.indicators.graph.EdgeWeightStrokeFunction;
import org.talend.dq.indicators.graph.GraphBuilder;
import org.talend.utils.color.AWTColorUtils;

import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.DefaultToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.PersistentLayout;
import edu.uci.ics.jung.visualization.PersistentLayoutImpl;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.GraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class JungGraphGenerator {

    protected static Logger log = Logger.getLogger(JungGraphGenerator.class);

    private static final int DOUBLE_CLICK_COUNT = 2;

    private static final String PERSIST_LAYOUT_FILE_NAME = "persistLayout.out";

    private Graph graph;

    private GraphBuilder graphbuilder;

    private PluggableRenderer pr;

    private VisualizationViewer vv;

    /**
     * DOC bzhou JungGraphGenerator constructor comment.
     */
    public JungGraphGenerator(GraphBuilder graphbuilder, List<Object[]> listRows) {
        this.graphbuilder = graphbuilder;
        graph = graphbuilder.createMultiGraph(listRows);
    }

    public Composite generate(Composite parent, boolean isWithHelp) {

        Composite frameComp = createAWTSWTComposite(parent);
        Frame frame = SWT_AWT.new_Frame(frameComp);
        frame.setTitle("Nominal Analysis");

        pr = new PluggableRenderer();
        configurePlugableRender(pr);

        PersistentLayout layout = new PersistentLayoutImpl(new FRLayout(graph));
        vv = new VisualizationViewer(layout, pr);

        configureVViewer(vv);

        // create a form to hold the graph
        JPanel controllers = createToolControllers(vv);

        JPanel panel = null;
        if (isWithHelp) {
            VisualizationViewer satellite = new SatelliteVisualizationViewer(vv, layout, pr, new Dimension(200, 200));

            panel = new JPanel(new BorderLayout());
            final JDesktopPane desktop = new JDesktopPane();

            JInternalFrame vvFrame = new JInternalFrame();
            vvFrame.setTitle("Desktop");
            vvFrame.setMaximizable(true);
            vvFrame.getContentPane().add(vv);
            vvFrame.pack();
            vvFrame.setVisible(true); // necessary as of 1.3
            try {
                vvFrame.setSelected(true);
            } catch (java.beans.PropertyVetoException ex) {
                log.error(ex, ex);
            }
            desktop.add(vvFrame);

            final JInternalFrame dialog = createSatelliteDialog(vv, satellite);

            JButton showSatellite = new JButton("Show Satellite View");
            showSatellite.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dialog.pack();
                    dialog.setLocation(desktop.getWidth() - dialog.getWidth(), 0);
                    dialog.show();
                    try {
                        dialog.setSelected(true);
                    } catch (java.beans.PropertyVetoException ex) {
                        log.error(ex, ex);
                    }
                }
            });
            controllers.add(showSatellite);
            desktop.add(dialog);

            panel.add(desktop);
        } else {
            panel = new GraphZoomScrollPane(vv);
        }

        frame.add(panel);
        frame.add(controllers, BorderLayout.SOUTH);
        frame.validate();

        addListeners(vv);

        return frameComp;
    }

    /**
     * DOC bZhou Comment method "createToolControllers".
     * 
     * @param vv
     * @return
     */
    private JPanel createToolControllers(final VisualizationViewer vv) {
        final GraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);

        JPanel pSlider = new JPanel();
        pSlider.setLayout(new BoxLayout(pSlider, BoxLayout.Y_AXIS));
        pSlider.setBorder(new TitledBorder("Filter edge weight"));

        final JSlider slider = new JSlider(0, 10, 0);
        slider.setMajorTickSpacing(2);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setSnapToTicks(true);
        slider.addChangeListener(new ChangeListener() {

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
             */
            public void stateChanged(ChangeEvent e) {
                int weight = slider.getValue();
                EdgeDisplayPredicate edgePredicate = (EdgeDisplayPredicate) pr.getEdgeIncludePredicate();
                edgePredicate.filterSmall(weight);
                // ((VertexDisplayPredicate) pr.getVertexIncludePredicate()).filterSmall(true, weight);
                // log.info("sliding..." + degree);
            }
        });
        pSlider.add(slider);

        final JCheckBox inverse = new JCheckBox("inverse edge weight");
        inverse.addActionListener(new ActionListener() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                graphbuilder.setProportionalWidth(inverse.isSelected());
                vv.repaint();
                log.info("inverse...");
            }
        });

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });
        JButton reset = new JButton("reset");
        reset.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                vv.getLayoutTransformer().setToIdentity();
                vv.getViewTransformer().setToIdentity();

            }
        });

        // set mode seleciton box
        final JCheckBox checkBox = new JCheckBox("Picking");
        checkBox.addActionListener(new ActionListener() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                DefaultModalGraphMouse modalGraphMouse = (DefaultModalGraphMouse) graphMouse;
                if (checkBox.isSelected()) {
                    modalGraphMouse.setMode(Mode.PICKING);
                } else {
                    modalGraphMouse.setMode(Mode.TRANSFORMING);
                }
            }
        });

        JButton persist = new JButton("Save Layout");
        persist.addActionListener(new ActionListener() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                // FIXME open file select dialog.
                // JFileChooser fileChooser = new JFileChooser();
                // fileChooser.setDialogTitle("Choose a file");
                // int result = fileChooser.showOpenDialog((JButton) e.getSource());
                //
                // if (result == JFileChooser.APPROVE_OPTION) {
                // File file = fileChooser.getSelectedFile();
                // }

                try {
                    PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                    pl.persist(PERSIST_LAYOUT_FILE_NAME);
                } catch (IOException e1) {
                    log.error(e1, e1);
                }
            }
        });

        JButton restore = new JButton("Restore Layout");
        restore.addActionListener(new ActionListener() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
             */
            public void actionPerformed(ActionEvent e) {
                try {
                    PersistentLayout pl = (PersistentLayout) vv.getGraphLayout();
                    pl.restore(PERSIST_LAYOUT_FILE_NAME);
                    vv.repaint();
                } catch (Exception e1) {
                    log.error(e1, e1);
                }
            }
        });

        JPanel jp2 = new JPanel();
        jp2.setLayout(new java.awt.GridLayout(2, 1));
        jp2.add(inverse);
        jp2.add(checkBox);

        JPanel jp3 = new JPanel();
        jp3.setLayout(new java.awt.GridLayout(2, 1));
        jp3.add(persist);
        jp3.add(restore);

        JPanel controls = new JPanel();
        controls.add(pSlider);
        controls.add(plus);
        controls.add(minus);
        controls.add(reset);
        controls.add(jp2);
        controls.add(jp3);

        return controls;
    }

    /**
     * DOC bZhou Comment method "configureVViewer".
     * 
     * @param vv
     */
    private void configureVViewer(final VisualizationViewer vv) {
        // vv = new VisualizationViewer(new CircleLayout(graph), pr);
        // vv = new VisualizationViewer(new SpringLayout(graph), pr);
        // vv = new VisualizationViewer(new KKLayout(graph), pr);
        vv.setBackground(Color.white);
        vv.setPickSupport(new ShapePickSupport());

        vv.addPostRenderPaintable(new VisualizationViewer.Paintable() {

            int x;

            int y;

            Font font;

            FontMetrics metrics;

            int swidth;

            int sheight;

            String str = "";

            public void paint(Graphics g) {
                Dimension d = vv.getSize();
                if (font == null) {
                    font = new Font(g.getFont().getName(), Font.BOLD, 30);
                    metrics = g.getFontMetrics(font);
                    swidth = metrics.stringWidth(str);
                    sheight = metrics.getMaxAscent() + metrics.getMaxDescent();
                    x = (d.width - swidth) / 2;
                    y = (int) (d.height - sheight * 1.5);
                }
                g.setFont(font);
                Color oldColor = g.getColor();
                g.setColor(Color.DARK_GRAY);
                g.drawString(str, x, y);
                g.setColor(oldColor);
            }

            public boolean useTransform() {
                return false;
            }
        });

        // vv.addGraphMouseListener(new TestGraphMouseListener());

        // add my listener for ToolTips
        vv.setToolTipFunction(new DefaultToolTipFunction());
    }

    /**
     * DOC bZhou Comment method "createSatelliteView".
     * 
     * @param vv
     * @param satellite
     * @return
     */
    private JInternalFrame createSatelliteDialog(final VisualizationViewer vv, VisualizationViewer satellite) {
        final JInternalFrame dialog = new JInternalFrame();
        dialog.setTitle("Satellite");
        Container content = dialog.getContentPane();

        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });
        JButton dismiss = new JButton("Dismiss");
        dismiss.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JOptionPane.showInternalMessageDialog(dialog, "Instructions", "Instructions", JOptionPane.PLAIN_MESSAGE);
            }
        });
        JPanel controls = new JPanel(new java.awt.GridLayout(2, 2));
        controls.add(plus);
        controls.add(minus);
        controls.add(dismiss);
        controls.add(help);
        content.add(satellite);
        content.add(controls, BorderLayout.SOUTH);
        return dialog;
    }

    /**
     * DOC BZhou Comment method "configurePlugabelRender".
     * 
     * @param graphbuilder
     * @param pr
     */
    private void configurePlugableRender(PluggableRenderer pr) {
        pr.setEdgeShapeFunction(new EdgeShape.Line());

        pr.setVertexPaintFunction(new VertexPaintFunction() {

            public Paint getFillPaint(Vertex v) {
                final Object userDatum = v.getUserDatum(GraphBuilder.COLUMN_IDX_KEY);
                if (userDatum != null) {
                    Integer colIndex = (Integer) userDatum;
                    return AWTColorUtils.getColor(colIndex);
                }
                return null;
            }

            public Paint getDrawPaint(Vertex v) {
                return Color.BLACK;
            }
        });

        pr.setVertexStringer(new VertexStringer() {

            public String getLabel(ArchetypeVertex v) {
                final Object userDatum = v.getUserDatum(GraphBuilder.V_LABEL_KEY);
                if (userDatum != null) {
                    return (String) userDatum;
                }
                return null;
            }
        });

        // pr.setEdgeStrokeFunction(new EdgeStrokeFunction() {
        //
        // public Stroke getStroke(Edge e) {
        // Integer weight = graphbuilder.getEdgeWeight().getNumber(e).intValue();
        // // (Integer) e.getUserDatum(GraphBuilder.E_WEIGHT_KEY);
        // // enlarge edges with small weight
        //
        // // return new BasicStroke(10 * weight / graphbuilder.getTotalWeight());
        // return new BasicStroke(10.0f / weight);
        // }
        // });

        pr.setEdgePaintFunction(new EdgePaintFunction() {

            public Paint getFillPaint(Edge e) {
                return null;
            }

            public Paint getDrawPaint(Edge e) {
                return getInternalPaint(e);
            }

            private Paint getInternalPaint(Edge e) {
                final Object userDatum = e.getUserDatum(GraphBuilder.E_ROWNUM_KEY);
                if (userDatum != null) {
                    Integer color = (Integer) userDatum;
                    return AWTColorUtils.getColor(color);
                }
                return Color.GRAY;
            }
        });

        pr.setEdgeStringer(new EdgeStringer() {

            public String getLabel(ArchetypeEdge arg0) {
                return String.valueOf(graphbuilder.getEdgeWeight().getNumber(arg0));
                // final Object userDatum = arg0.getUserDatum(GraphBuilder.E_LABEL_KEY);
                // if (userDatum != null) {
                // return (String) userDatum;
                // }
                // return null;
            }
        });

        pr.setEdgeStrokeFunction(new EdgeWeightStrokeFunction(graphbuilder));

        pr.setEdgeIncludePredicate(new EdgeDisplayPredicate(graphbuilder));

        // pr.setVertexIncludePredicate(new VertexDisplayPredicate(false));
    }

    private Composite createAWTSWTComposite(Composite parent) {
        Composite frameComp = new Composite(parent, SWT.EMBEDDED);
        frameComp.setLayout(new GridLayout());
        frameComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        frameComp.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

        return frameComp;
    }

    /**
     * DOC BZhou Comment method "addListeners".
     */
    private void addListeners(final VisualizationViewer vv) {
        vv.addMouseListener(new MouseAdapter() {

            /*
             * (non-Javadoc)
             * 
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == DOUBLE_CLICK_COUNT) {
                    // show in full screem.
                    showFullDialog();
                }

                if (e.getButton() == MouseEvent.BUTTON3) {
                    // show menu.
                    PopupMenu menu = new PopupMenu();
                    MenuItem item = new MenuItem("show in fullscreem");
                    menu.add(item);
                    vv.add(menu);
                    menu.show(vv, e.getX(), e.getY());

                    item.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            showFullDialog();

                        }
                    });
                }
            }

            /**
             * DOC BZhou Comment method "showFullDialog".
             */
            private void showFullDialog() {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {

                        new JungGraphChartDialog(JungGraphGenerator.this).open();
                    }
                });
            }
        });

    }

}
