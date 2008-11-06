// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * @author rli
 * 
 */
public class DataFilterComp extends AbstractPagePart {

    private final String stringDataFilter;

    private Text dataFilterText;

    public DataFilterComp(Composite parent, String stringDataFilter) {
        this.stringDataFilter = stringDataFilter;
        this.createContent(parent);
    }

    public void createContent(Composite parent) {
        parent.setLayout(new GridLayout(3, true));

        dataFilterText = new Text(parent, SWT.BORDER | SWT.MULTI);
        dataFilterText.setToolTipText(DefaultMessagesImpl.getString("DataFilterComp.here")); //$NON-NLS-1$
        dataFilterText.setText(stringDataFilter == null ? PluginConstant.EMPTY_STRING : stringDataFilter);
        GridDataFactory.fillDefaults().span(2, 3).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(dataFilterText);
        ((GridData) dataFilterText.getLayoutData()).heightHint = 150;
        dataFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                setDirty(true);
                propertyChangeSupport.firePropertyChange(PluginConstant.DATAFILTER_PROPERTY, null, dataFilterText.getText());
            }

        });

        Composite buttonsComp = new Composite(parent, SWT.None);
        GridDataFactory.fillDefaults().span(1, 1).applyTo(buttonsComp);
        buttonsComp.setLayout(new GridLayout(1, true));

        buttonsComp.setVisible(false);

        Button button = new Button(buttonsComp, SWT.None);
        button.setText(DefaultMessagesImpl.getString("DataFilterComp.edit")); //$NON-NLS-1$
        GridDataFactory.fillDefaults().span(1, 1).align(SWT.FILL, SWT.TOP).applyTo(button);
    }

    public String getDataFilterString() {
        return dataFilterText.getText();
    }

}
