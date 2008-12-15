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
package org.talend.dataprofiler.core.ui.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 * DOC Zqin class global comment. Detailled comment
 */
public class DateTimeDialog extends TrayDialog {

    private DateTime timec;

    private String selectDate;

    /**
     * DOC Zqin DateTimeDialog constructor comment.
     * 
     * @param shell
     */
    public DateTimeDialog(Shell shell) {
        super(shell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new FillLayout());
        timec = new DateTime(top, SWT.CALENDAR);
        return top;
    }

    @Override
    protected void okPressed() {
        int year = timec.getYear();
        int month = timec.getMonth();
        int day = timec.getDay();

        Calendar cenlendar = Calendar.getInstance();
        cenlendar.set(year, month, day);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        selectDate = format.format(cenlendar.getTime());

        super.okPressed();
    }

    public String getSelectDate() {
        return this.selectDate;
    }
}
