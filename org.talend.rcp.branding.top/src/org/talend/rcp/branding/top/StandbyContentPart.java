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
package org.talend.rcp.branding.top;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.cheatsheets.CheatSheetViewerFactory;
import org.eclipse.ui.cheatsheets.ICheatSheetViewer;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.intro.config.IStandbyContentPart;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class StandbyContentPart implements IStandbyContentPart {

    private static String MEMENTO_CHEATSHEET_ID_ATT = "cheatsheetId"; //$NON-NLS-1$

    // private IIntroPart introPart;
    private ICheatSheetViewer viewer;

    private Composite container;

    private String input;

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#init(org.eclipse.ui.intro.IIntroPart)
     */
    public void init(IIntroPart introPart, IMemento memento) {
        // this.introPart = introPart;
        // try to restore last state.
        input = getCachedInput(memento);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#createControl(org.eclipse.swt.widgets.Composite,
     * org.eclipse.ui.forms.widgets.FormToolkit)
     */
    public void createPartControl(Composite parent, FormToolkit toolkit) {
        container = toolkit.createComposite(parent);
        FillLayout layout = new FillLayout();
        layout.marginWidth = layout.marginHeight = 0;
        container.setLayout(layout);

        // IViewPart findView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(
        // PluginConstant.CHEAT_SHEET_VIEW);
        // if (findView != null) {
        // viewer = (ICheatSheetViewer) findView;
        // } else {
        viewer = CheatSheetViewerFactory.createCheatSheetView();
        // }
        viewer.createPartControl(container);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#getControl()
     */
    public Control getControl() {
        return container;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#setInput(java.lang.Object)
     */
    public void setInput(Object input) {
        // if the new input is null, use cacched input from momento.
        if (input != null)
            this.input = (String) input;
        viewer.setInput(this.input);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#setFocus()
     */
    public void setFocus() {
        viewer.setFocus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.internal.parts.IStandbyContentPart#dispose()
     */
    public void dispose() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.intro.config.IStandbyContentPart#saveState(org.eclipse.ui.IMemento)
     */
    public void saveState(IMemento memento) {
        String currentCheatSheetId = viewer.getCheatSheetID();
        if (currentCheatSheetId != null)
            memento.putString(MEMENTO_CHEATSHEET_ID_ATT, currentCheatSheetId);
    }

    /**
     * Tries to create the last content part viewed, based on content part id..
     * 
     * @param memento
     * @return
     */
    private String getCachedInput(IMemento memento) {
        if (memento == null)
            return null;
        return memento.getString(MEMENTO_CHEATSHEET_ID_ATT);

    }
}
