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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 * DOC Zqin class global comment. Detailled comment
 */

public class DateTimeDialog extends TrayDialog {

    private DateTime dateTime;

    private boolean isDatetime;

    private String selectDate;

    /**
     * DOC Zqin DateTimeDialog constructor comment.
     * 
     * @param shell
     */
    public DateTimeDialog(Shell shell, boolean isDatetime) {
        super(shell);
        this.isDatetime = isDatetime;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Date Selector");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout());

        // Label dl = new Label(top, SWT.NONE);
        // dl.setText(DefaultMessagesImpl.getString("DateTimeDialog.SetDate")); //$NON-NLS-1$
        // dl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        dateTime = new DateTime(top, SWT.CALENDAR);
        dateTime.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (isDatetime) {
            // Label dt = new Label(top, SWT.NONE);
            // dt.setText(DefaultMessagesImpl.getString("DateTimeDialog.SetTime")); //$NON-NLS-1$
            dateTime = new DateTime(top, SWT.TIME | SWT.BORDER);
            dateTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        return top;
    }

    @Override
    protected void okPressed() {
        Calendar cenlendar = Calendar.getInstance();
        SimpleDateFormat format = null;

        int year = dateTime.getYear();
        int month = dateTime.getMonth();
        int day = dateTime.getDay();
        int hour = dateTime.getHours();
        int mnts = dateTime.getMinutes();
        int secds = dateTime.getSeconds();

        cenlendar.set(year, month, day, hour, mnts, secds);

        if (isDatetime) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
        }

        selectDate = format.format(cenlendar.getTime());

        super.okPressed();
    }

    public String getSelectDate() {
        return this.selectDate;
    }
}
