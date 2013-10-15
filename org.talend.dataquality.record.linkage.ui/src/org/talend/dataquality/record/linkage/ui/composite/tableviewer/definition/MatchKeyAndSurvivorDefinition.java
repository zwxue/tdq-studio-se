// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.record.linkage.ui.composite.tableviewer.definition;

import org.apache.commons.lang.StringUtils;
import org.talend.dataquality.rules.MatchKeyDefinition;
import org.talend.dataquality.rules.SurvivorshipKeyDefinition;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class MatchKeyAndSurvivorDefinition {

    MatchKeyDefinition matchKey;

    SurvivorshipKeyDefinition survivorShipKey;

    public MatchKeyAndSurvivorDefinition() {

    }

    public MatchKeyDefinition getMatchKey() {
        return this.matchKey;
    }

    // when the Table viewer remove a row, need to compare if it is equals
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MatchKeyAndSurvivorDefinition
                && StringUtils.equals(((MatchKeyAndSurvivorDefinition) obj).getMatchKey().getName(), this.matchKey.getName())) {
            return true;
        } else {
            return false;
        }
    }

    public void setMatchKey(MatchKeyDefinition matchKey) {
        this.matchKey = matchKey;
    }

    public SurvivorshipKeyDefinition getSurvivorShipKey() {
        return this.survivorShipKey;
    }

    public void setSurvivorShipKey(SurvivorshipKeyDefinition survivorShipKey) {
        this.survivorShipKey = survivorShipKey;
    }

}
