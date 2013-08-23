// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.section;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.RefreshChartAction;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 *
 */
public abstract class AbstractMatchAnaysisTableSection extends AbstractSectionComposite implements ITableEditOperation {

    protected List<Object[]> matchRows = new ArrayList<Object[]>();

    protected Map<String, String> columnMap = null;

    protected Analysis analysis = null;

    protected Boolean isNeedSubChart = true;

    /**
     * @param parent
     * @param style
     */
    public AbstractMatchAnaysisTableSection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit,
            Analysis analysis) {
        super(form, parent, style, toolkit);
        this.analysis = analysis;
    }

    /**
     * DOC zshen Comment method "createContext".
     */
    public Composite createContent() {
        section.setText(getSectionName());
        Composite sectionClient = toolkit.createComposite(section, SWT.NONE);
        sectionClient.setLayout(new GridLayout(getColumnNum(), true));
        sectionClient.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite createSubContent = createSubContent(sectionClient);
        createButtons(createSubContent);
        if (isNeedSubChart()) {
            createSubChart(sectionClient);
        }
        section.setClient(sectionClient);
        return sectionClient;
    }

    /**
     * Getter for isNeedSubChart.
     *
     * @return the isNeedSubChart
     */
    public Boolean isNeedSubChart() {
        return this.isNeedSubChart;
    }


    /**
     * Sets the isNeedSubChart.
     *
     * @param isNeedSubChart the isNeedSubChart to set
     */
    public void setIsNeedSubChart(Boolean isNeedSubChart) {
        this.isNeedSubChart = isNeedSubChart;
    }

    /**
     * DOC zshen Comment method "getColumnNum".
     *
     * @return
     */
    private int getColumnNum() {
        if (isNeedSubChart()) {
            return 2;
        } else {
            return 1;
        }
    }

    protected void createButtons(Composite sectionClient) {
        if (isNeedSubChart) {
            createRefreshButton(sectionClient);
        } else {
            createEditOperationButtons(sectionClient);
        }
    }

    /**
     * DOC zshen Comment method "createEditOperationButtons".
     *
     * @param sectionClient
     */
    private void createEditOperationButtons(Composite parent) {
        Composite buttonsComposite = new Composite(parent, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(7, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        final Button addButton = new Button(buttonsComposite, SWT.NONE);
        addButton.setToolTipText("Add New Item");
        addButton.setImage(ImageLib.getImage(ImageLib.ADD_ACTION));
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                addTableItem();
            }
        });

        final Button delButton = new Button(buttonsComposite, SWT.NONE);
        delButton.setToolTipText("Delete Selcetion Item");
        delButton.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));
        delButton.setLayoutData(labelGd);
        delButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                removeTableItem();
            }
        });
        final Button upButton = new Button(buttonsComposite, SWT.NONE);
        upButton.setToolTipText("Move Item Up");
        upButton.setImage(ImageLib.getImage(ImageLib.UP_ACTION));
        upButton.setLayoutData(labelGd);
        upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveUpTableItem();
            }
        });

        final Button downButton = new Button(buttonsComposite, SWT.NONE);
        downButton.setToolTipText("Move Item Down");
        downButton.setImage(ImageLib.getImage(ImageLib.DOWN_ACTION));
        downButton.setLayoutData(labelGd);
        downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                moveDownTableItem();
            }
        });
        final Button copyButton = new Button(buttonsComposite, SWT.NONE);
        copyButton.setToolTipText("Copy Selcetion Item");
        copyButton.setImage(ImageLib.getImage(ImageLib.COPY_ACTION));
        copyButton.setLayoutData(labelGd);
        copyButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                copyTableItem();

            }
        });

        final Button pasteButton = new Button(buttonsComposite, SWT.NONE);
        pasteButton.setToolTipText("Paste Selcetion Item");
        pasteButton.setImage(ImageLib.getImage(ImageLib.PASTE_ACTION));
        pasteButton.setLayoutData(labelGd);
        pasteButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                pasteTableItem();

            }
        });


    }



    /**
     * DOC zshen Comment method "createButtons".
     */
    private void createRefreshButton(Composite sectionClient) {

        Composite buttonsComposite = new Composite(sectionClient, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(7, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        final Button refresh = new Button(buttonsComposite, SWT.NONE);
        refresh.setToolTipText("Add New Item"); //$NON-NLS-1$
        refresh.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        refresh.setLayoutData(labelGd);
        refresh.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new RefreshChartAction(AbstractMatchAnaysisTableSection.this).run();
            }
        });

    }

    public void setDataInput(List<Object[]> allData) {
        matchRows = allData;
    }

    public void setColumnNameInput(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#pasteTableItem()
     */
    @Override
    public void pasteTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#copyTableItem()
     */
    @Override
    public void copyTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveDownTableItem()
     */
    @Override
    public void moveDownTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#moveUpTableItem()
     */
    @Override
    public void moveUpTableItem() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#removeTableItem()
     */
    @Override
    public void removeTableItem() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.record.linkage.ui.section.AbstractMatchAnaysisTableSection#addTableItem()
     */
    @Override
    public void addTableItem() {

    }



    /**
     * DOC zshen Comment method "createSubChart". TODO re-order the protected and abstract key word. let "protected"
     * first.
     *
     * @param sectionClient
     */
    abstract protected void createSubChart(Composite sectionClient);

    /**
     * DOC zshen Comment method "createSubContent".
     *
     * @param sectionClient
     */
    abstract protected Composite createSubContent(Composite sectionClient);

    /**
     * DOC zshen Comment method "getSectionName".
     *
     * @return
     */
    abstract protected String getSectionName();

    public abstract void refreshChart();

    public abstract Boolean isKeyDefinitionAdded(String columnName) throws Exception;

}
