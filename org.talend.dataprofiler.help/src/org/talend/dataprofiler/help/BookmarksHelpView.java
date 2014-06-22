package org.talend.dataprofiler.help;

import org.eclipse.help.ui.internal.IHelpUIConstants;
import org.eclipse.help.ui.internal.views.HelpView;

@SuppressWarnings("restriction")
public class BookmarksHelpView extends HelpView {

    public static final String HELP_VIEW_ID = "org.talend.dataprofiler.help.BookmarksHelpView"; //$NON-NLS-1$

    @Override
    protected String getFirstPage() {
        return IHelpUIConstants.HV_BOOKMARKS_PAGE;
    }

    public BookmarksHelpView() {
        // TODO Auto-generated constructor stub
    }

}
