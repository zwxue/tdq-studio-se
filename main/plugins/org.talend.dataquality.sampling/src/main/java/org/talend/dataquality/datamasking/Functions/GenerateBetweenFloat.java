// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.datamasking.Functions;

/**
 * created by jgonzalez on 18 juin 2015. This function will return a float between the two given as parameters.
 *
 */
public class GenerateBetweenFloat extends GenerateBetween<Float> {

    @Override
    public Float generateMaskedRow(Float f) {
        if (f == null && keepNull) {
            return null;
        } else {
            super.setBounds();
            return (float) rnd.nextInt((max - min) + 1) + min;
        }
    }
}