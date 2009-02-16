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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DateTimeDialog extends TrayDialog {

    private DateTime timed;

    private DateTime timet;

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
    protected Control createDialogArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(2, false));

        Label dl = new Label(top, SWT.NONE);
        dl.setText(DefaultMessagesImpl.getString("DateTimeDialog.SetDate")); //$NON-NLS-1$
        dl.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
        timed = new DateTime(top, SWT.CALENDAR);
        timed.setLayoutData(new GridData(GridData.FILL_BOTH));

        if (isDatetime) {
            Label dt = new Label(top, SWT.NONE);
            dt.setText(DefaultMessagesImpl.getString("DateTimeDialog.SetTime")); //$NON-NLS-1$
            timet = new DateTime(top, SWT.TIME);
            timet.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }

        return top;
    }

    @Override
    protected void okPressed() {
        Calendar cenlendar = Calendar.getInstance();
        SimpleDateFormat format = null;

        int year = timed.getYear();
        int month = timed.getMonth();
        int day = timed.getDay();

        if (timet != null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

            int hour = timet.getHours();
            int mnts = timet.getMinutes();
            int secds = timet.getSeconds();

            cenlendar.set(year, month, day, hour, mnts, secds);
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
            cenlendar.set(year, month, day);
        }

        selectDate = format.format(cenlendar.getTime());

        super.okPressed();
    }

    public String getSelectDate() {
        return this.selectDate;
    }
}
