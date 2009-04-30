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

import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.objectmodel.core.Expression;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public final class ColumnIndicatorRule {

    private ColumnIndicatorRule() {
    }

    public static boolean match(IIndicatorNode node, ColumnIndicator columnIndicator) {

        IndicatorEnum indicatorType = node.getIndicatorEnum();
        TdColumn column = columnIndicator.getTdColumn();

        if (indicatorType == null) {

            for (IIndicatorNode one : node.getChildren()) {
                if (match(one, columnIndicator)) {
                    return true;
                }
            }

            return false;
        }

        return patternRule(indicatorType, column);
    }

    public static boolean patternRule(IndicatorEnum indicatorType, TdColumn column) {

        int javaType = column.getJavaType();
        DataminingType dataminingType = MetadataHelper.getDataminingType(column);
        if (dataminingType == null) {
            dataminingType = MetadataHelper.getDefaultDataminingType(javaType);
        }

        switch (indicatorType) {
        case CountsIndicatorEnum:
        case RowCountIndicatorEnum:
        case NullCountIndicatorEnum:
        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            // MOD scorreia 2008-06-04 enable distinct count, unique count and duplicate count for all types
            // if (dataminingType == DataminingType.NOMINAL) {
            return true;
        case DefValueCountIndicatorEnum:
            Expression initialValue = column.getInitialValue();
            if (initialValue != null && initialValue.getBody() != null) {
                // MOD scorreia 2009-04-21 bug 6979
                // non nullable numeric column give a non null default value as ''
                return initialValue.getBody().length() != 0 || Java2SqlType.isTextInSQL(javaType);
            }

            break;
        case BlankCountIndicatorEnum:
        case TextIndicatorEnum:
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:

            if (Java2SqlType.isTextInSQL(javaType)) {
                if (dataminingType == DataminingType.NOMINAL || dataminingType == DataminingType.UNSTRUCTURED_TEXT) {
                    return true;
                }
            }

            break;

        case ModeIndicatorEnum:
        case FrequencyIndicatorEnum:
        case LowFrequencyIndicatorEnum:
        case PatternFreqIndicatorEnum:
        case PatternLowFreqIndicatorEnum:
            if (dataminingType == DataminingType.NOMINAL || dataminingType == DataminingType.INTERVAL) {
                return true;
            }

            break;
        // MOD mzhao 2009-03-05 Soundex frequency indicator
        case SoundexIndicatorEnum:
        case SoundexLowIndicatorEnum:
            // MOD mzhao 2009-03-05 do not allow soundex on date field because it is not correctly handle in database.
            if (!Java2SqlType.isDateInSQL(javaType)
                    && (dataminingType == DataminingType.NOMINAL || dataminingType == DataminingType.INTERVAL)) {
                return true;
            }
            break;
        case MeanIndicatorEnum:
        case MedianIndicatorEnum:
        case BoxIIndicatorEnum:
        case IQRIndicatorEnum:
        case LowerQuartileIndicatorEnum:
        case UpperQuartileIndicatorEnum:

            // MOD scorreia 2008-09-19 do not allow box plot on date fields because it is not correctly handled in the
            // graphics and database yet.
            if (Java2SqlType.isNumbericInSQL(javaType) /* || Java2SqlType.isDateInSQL(javaType) */) {
                if (dataminingType == DataminingType.INTERVAL) {
                    return true;
                }
            }
            break;

        case RangeIndicatorEnum:
        case MinValueIndicatorEnum:
        case MaxValueIndicatorEnum:
            // MOD scorreia 2008-09-25 do not allow min and max on date fields because it is not correctly handled in
            // the graphics and database yet.
            if (Java2SqlType.isNumbericInSQL(javaType) || Java2SqlType.isDateInSQL(javaType)) {
                if (dataminingType == DataminingType.INTERVAL) {
                    return true;
                }
            }

            break;

        default:
            return false;
        }

        return false;
    }
}
