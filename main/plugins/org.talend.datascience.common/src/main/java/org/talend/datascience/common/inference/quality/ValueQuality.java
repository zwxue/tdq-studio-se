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
package org.talend.datascience.common.inference.quality;

public class ValueQuality {

    private long validCount;

    private long emptyCount;

    private int invalidCount;

    public long getValidCount() {
        return validCount;
    }

    public long getInvalidCount() {
        return invalidCount;
    }

    public long getCount() {
        return validCount + invalidCount + emptyCount;
    }

    public long getEmptyCount() {
        return emptyCount;
    }

    public void incrementEmpty() {
        emptyCount++;
    }

    public void incrementValid() {
        validCount++;
    }

    public void incrementInvalid() {
        invalidCount++;
    }
}
