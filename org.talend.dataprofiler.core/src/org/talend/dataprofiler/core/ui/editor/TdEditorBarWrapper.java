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

package org.talend.dataprofiler.core.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 * DOC mzhao TdEditorToolBar class global comment. Detailled comment
 */
public class TdEditorBarWrapper {

    private List<Section> sectionLs = null;

    public void setSections(Section[] sections) {
        for (Section section : sections) {
            if (!sectionLs.contains(section)) {
                this.sectionLs.add(section);
            }

        }
    }

    public TdEditorBarWrapper() {
        sectionLs = new ArrayList<Section>();
    }

    public List<Section> getSections() {
        return sectionLs;
    }
}
