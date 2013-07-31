/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge;

/**
 *
 */
public enum MatchAlgorithm {
    LEVENSHTEIN,
    JAROWINKLER,
    JACCARD,
    SOUNDEX,
    DIFFERENCE,
    DATE_COMPARE_YEAR,
    DATE_COMPARE_YEAR_MONTH,
    DATE_COMPARE_YEAR_MONTH_DAY,
    COMPARE_DOUBLE,
    DOUBLE_METAPHONE
}
