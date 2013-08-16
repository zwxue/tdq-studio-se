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
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.record.linkage.ui.action.RefreshChartAction;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;

/**
 * created by zshen on Jul 31, 2013 Detailled comment
 * 
 */
public abstract class AbstractMatchTableSection {

    protected FormToolkit toolkit;

    protected List<String[]> tableData;

    protected Map<String, String> columnMap = null;

    protected Section section = null;

    protected Analysis analysis = null;

    /**
     * @param parent
     * @param style
     */
    public AbstractMatchTableSection(final ScrolledForm form, Composite parent, int style, FormToolkit toolkit, Analysis analysis) {

        this.toolkit = toolkit;
        this.section = this.toolkit.createSection(parent, style);
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
        section.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                form.reflow(true);
            }

        });
        section.setExpanded(true);
        this.analysis = analysis;
    }

    /**
     * DOC zshen Comment method "createContext".
     */
    public Composite createContent() {
        section.setText(getSectionName());
        Composite sectionClient = toolkit.createComposite(section, SWT.NONE);
        sectionClient.setLayout(new GridLayout(2, true));
        section.setClient(sectionClient);
        createSubContent(sectionClient);
        createSubChart(sectionClient);
        return sectionClient;
    }

    /**
     * DOC zshen Comment method "createButtons".
     */
    protected void createRefrshButton(Composite sectionClient) {

        Composite buttonsComposite = new Composite(sectionClient, SWT.NONE);
        buttonsComposite.setLayout(new GridLayout(7, true));

        GridData labelGd = new GridData();
        labelGd.horizontalAlignment = SWT.LEFT;
        labelGd.widthHint = 30;

        final Button addButton = new Button(buttonsComposite, SWT.NONE);
        addButton.setToolTipText("Add New Item"); //$NON-NLS-1$
        addButton.setImage(ImageLib.getImage(ImageLib.SECTION_PREVIEW));
        addButton.setLayoutData(labelGd);
        addButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new RefreshChartAction(AbstractMatchTableSection.this).run();
            }
        });

    }

    public void setDataInput(List<Object[]> allData) {
        tableData = new ArrayList<String[]>();
        for (Object[] row : allData) {
            String[] stringRow = new String[row.length];
            int i = 0;
            for (Object data : row) {
                if (data == null) {
                    stringRow[i++] = "";
                } else {
                    stringRow[i++] = String.valueOf(data);
                }
            }
            tableData.add(stringRow);
        }
    }

    public void setColumnNameInput(Map<String, String> columnMap) {
        this.columnMap = columnMap;
    }

    /**
     * Getter for section.
     * 
     * @return the section
     */
    public Section getSection() {
        return this.section;
    }

    public void setClient(Control client) {
        this.section.setClient(client);
    }

    /**
     * DOC zshen Comment method "createSubChart".
     * 
     * @param sectionClient
     */
    abstract protected void createSubChart(Composite sectionClient);

    /**
     * DOC zshen Comment method "createSubContent".
     * 
     * @param sectionClient
     */
    abstract protected void createSubContent(Composite sectionClient);

    /**
     * DOC zshen Comment method "getSectionName".
     * 
     * @return
     */
    abstract protected String getSectionName();

    abstract public void refreshChart();

}
