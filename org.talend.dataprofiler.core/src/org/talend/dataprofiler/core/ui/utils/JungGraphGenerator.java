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

import java.awt.BasicStroke;
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
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationViewer.GraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.visualization.control.ScalingControl;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class JungGraphGenerator {

    private static final int DOUBLE_CLICK_COUNT = 2;

    private Graph graph;

    private GraphBuilder graphbuilder;

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

        PluggableRenderer pr = new PluggableRenderer();
        configurePlugableRender(pr);

        Layout layout = new ISOMLayout(graph);
        final VisualizationViewer vv = new VisualizationViewer(layout, pr);
        configureVViewer(vv);

        // create a frome to hold the graph
        final GraphMouse graphMouse = new DefaultModalGraphMouse();
        vv.setGraphMouse(graphMouse);

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
        JComboBox modeBox = ((DefaultModalGraphMouse) graphMouse).getModeComboBox();

        JPanel controls = new JPanel();
        controls.add(plus);
        controls.add(minus);
        controls.add(reset);
        controls.add(modeBox);

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
                ex.printStackTrace();
            }
            desktop.add(vvFrame);

            final JInternalFrame dialog = createSatelliteDialog(vv, satellite);

            JButton zoomer = new JButton("Show Satellite View");
            zoomer.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    dialog.pack();
                    dialog.setLocation(desktop.getWidth() - dialog.getWidth(), 0);
                    dialog.show();
                    try {
                        dialog.setSelected(true);
                    } catch (java.beans.PropertyVetoException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            controls.add(zoomer);
            desktop.add(dialog);

            panel.add(desktop);
        } else {
            panel = new GraphZoomScrollPane(vv);
        }

        frame.add(panel);
        frame.add(controls, BorderLayout.SOUTH);
        frame.validate();

        addListeners(vv);

        return frameComp;
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

        pr.setEdgeStrokeFunction(new EdgeStrokeFunction() {

            public Stroke getStroke(Edge e) {
                Integer weight = graphbuilder.getEdgeWeight().getNumber(e).intValue();
                // (Integer) e.getUserDatum(GraphBuilder.E_WEIGHT_KEY);
                // enlarge edges with small weight

                // return new BasicStroke(10 * weight / graphbuilder.getTotalWeight());
                return new BasicStroke(10.0f / weight);
            }
        });

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
