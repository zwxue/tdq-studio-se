package org.talend.dataprofiler.core.ui.dialog.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class BlockingKeysTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public static final String BLOCKING_KEY_NAME = "BLOCKING_KEY_NAME";

    public static final String PRECOLUMN = "PRECOLUMN";

    public static final String PRE_ALGO = "PRE_ALGO";

    public static final String PRE_VALUE = "PRE_VALUE";

    public static final String KEY_ALGO = "KEY_ALGO";

    public static final String KEY_VALUE = "KEY_VALUE";

    public static final String POST_ALGO = "POST_ALGO";

    public static final String POST_VALUE = "POST_VALUE";

    public Image getColumnImage(Object element, int columnIndex) {
        Image warn = null;
        Map<String, String> rule = (HashMap<String, String>) element;
        if (0 == columnIndex && containsRule(rule)) {
            warn = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
        }
        return warn;
    }

    protected boolean containsRule(Map<String, String> rule) {
        return false;
        // return getRuleNames().contains(rule.get(MATCH_KEY_NAME));
    }

    public String getColumnText(Object element, int columnIndex) {
        Map<String, String> rule = (HashMap<String, String>) element;
        String result = null;
        switch (columnIndex) {
        case 0:
            result = rule.get(BLOCKING_KEY_NAME);
            break;
        case 1:
            result = rule.get(PRECOLUMN);
            break;
        case 2:
            result = rule.get(PRE_ALGO);
            break;
        case 3:
            result = rule.get(PRE_VALUE);
            break;
        case 4:
            result = rule.get(KEY_ALGO);
            break;
        case 5:
            result = rule.get(KEY_VALUE);
            break;
        case 6:
            result = rule.get(POST_ALGO);
            break;
        case 7:
            result = rule.get(POST_VALUE);
            break;
        }
        return result;
    }
}
