// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.indicators.datavalidation;

import org.talend.cwm.indicator.DataValidation;

/**
 * created by talend on Oct 17, 2014 Detailled comment
 * 
 */
public interface IValidationFactor {

    public static IValidationFactor instance = ValidationFactorImpl.getInstance();

    public DataValidation createDataValidation();

    public DataValidation createDistinctDataValidation();

    public DataValidation createUniqueDataValidation();

    public DataValidation createRowDataValidation();

    public DataValidation createDuplicateDataValidation();
}
