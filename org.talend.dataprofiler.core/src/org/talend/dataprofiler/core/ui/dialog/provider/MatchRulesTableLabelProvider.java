package org.talend.dataprofiler.core.ui.dialog.provider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;

public class MatchRulesTableLabelProvider extends LabelProvider implements ITableLabelProvider {

    public static final String MATCH_KEY_NAME = MatchAnalysisConstant.MATCH_KEY_NAME;

    public static final String INPUT_COLUMN = MatchAnalysisConstant.INPUT_COLUMN;

    public static final String MATCHING_TYPE = MatchAnalysisConstant.MATCHING_TYPE;

    public static final String CUSTOM_MATCHER = MatchAnalysisConstant.CUSTOM_MATCHER;

    public static final String CONFIDENCE_WEIGHT = MatchAnalysisConstant.CONFIDENCE_WEIGHT;

    public static final String HANDLE_NULL = MatchAnalysisConstant.HANDLE_NULL;

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
        case 0: // match key name
            result = rule.get(MATCH_KEY_NAME);
            break;
        case 1: // column
            result = rule.get(INPUT_COLUMN);
            break;
        case 2: // matching type
            result = AttributeMatcherType.valueOf(rule.get(MATCHING_TYPE)).getLabel();
            break;
        case 3: // custom matcher class
            result = rule.get(CUSTOM_MATCHER);
            break;
        case 4: // confidence weight
            result = rule.get(CONFIDENCE_WEIGHT);
            break;
        case 5: // handle null
            result = rule.get(HANDLE_NULL);
            break;
        }
        return result;
    }
}
