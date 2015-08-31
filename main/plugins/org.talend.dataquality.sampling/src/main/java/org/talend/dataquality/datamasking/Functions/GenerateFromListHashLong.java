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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * created by jgonzalez on 24 juin 2015. See GenerateFromListHash.
 *
 */
public class GenerateFromListHashLong extends GenerateFromListHash<Long> implements Serializable {

    private static final long serialVersionUID = 5749650198929940526L;

    private List<Long> LongTokens = new ArrayList<>();

    @Override
    public Long generateMaskedRow(Long l) {
        if (l == null && keepNull) {
            return null;
        } else {
            super.init();
            for (int j = 0; j < StringTokens.size(); ++j) {
                long tmp = 0L;
                try {
                    tmp = Long.parseLong(StringTokens.get(j));
                } catch (NumberFormatException e) {
                    // Do Nothing
                }
                LongTokens.add(tmp);
            }
            if (LongTokens.size() > 0) {
                if (l == null) {
                    return LongTokens.get(rnd.nextInt(LongTokens.size()));
                } else {
                    return LongTokens.get(Math.abs(l.hashCode() % LongTokens.size()));
                }
            } else {
                return 0L;
            }
        }
    }
}
