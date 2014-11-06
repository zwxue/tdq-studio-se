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
public class ValidationFactorImpl implements IValidationFactor {

    private static IValidationFactor instance = null;

    ValidationFactorImpl() {
    }

    public static IValidationFactor getInstance() {
        if (instance == null) {
            instance = new ValidationFactorImpl();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.datavalidation.IValidationFactor#createDataValidation()
     */
    @Override
    public DataValidation createDataValidation() {
        return new DataValidationImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.datavalidation.IValidationFactor#createDistinctValidation()
     */
    @Override
    public DataValidation createDistinctDataValidation() {
        return new DistinctDataValidationImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.datavalidation.IValidationFactor#createUniqueDataValidation()
     */
    @Override
    public DataValidation createUniqueDataValidation() {
        return new UniqueDataValidationImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.datavalidation.IValidationFactor#createRowDataValidation()
     */
    @Override
    public DataValidation createRowDataValidation() {
        return new RowDataValidationImpl();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.datavalidation.IValidationFactor#createDuplicateDataValidation()
     */
    @Override
    public DataValidation createDuplicateDataValidation() {
        return new DuplicateDataValidationImpl();
    }
}
