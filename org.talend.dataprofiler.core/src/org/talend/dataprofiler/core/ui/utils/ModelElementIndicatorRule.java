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
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.resource.relational.Column;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class ModelElementIndicatorRule {

    private ModelElementIndicatorRule() {
    }

    public static boolean match(IIndicatorNode node, ModelElementIndicator meIndicator, ExecutionLanguage language) {

        IndicatorEnum indicatorType = node.getIndicatorEnum();
        ModelElement me = meIndicator.getModelElement();

        if (indicatorType == null) {

            for (IIndicatorNode one : node.getChildren()) {
                if (match(one, meIndicator, language)) {
                    return true;
                }
            }

            return false;
        }

        return patternRule(indicatorType, me, language);
    }

    public static boolean patternRule(IndicatorEnum indicatorType, ModelElement me, ExecutionLanguage language) {

        int javaType = 0;
        if (me instanceof Column) {
            javaType = ((TdColumn) me).getJavaType();
        }
        DataminingType dataminingType = MetadataHelper.getDataminingType(me);
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
            Expression initialValue = null;
            if (me instanceof TdColumn) {
                initialValue = ((TdColumn) me).getInitialValue();
            }
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
        // MOD zshen 2010-01-27 Date Pattern frequency indicator
        case DatePatternFreqIndicatorEnum:
            if (ExecutionLanguage.JAVA.equals(language)
                    && (Java2SqlType.isDateInSQL(javaType) || Java2SqlType.isTextInSQL(javaType) || Java2SqlType
                            .isDateTimeSQL(javaType))) {
                return true;
            }
            break;
        // MOD mzhao 2009-03-05 Soundex frequency indicator
        case SoundexIndicatorEnum:
        case SoundexLowIndicatorEnum:
            if (!Java2SqlType.isDateInSQL(javaType) && !Java2SqlType.isNumbericInSQL(javaType)
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
