// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.builders;

import java.sql.Connection;
import java.sql.SQLException;

import org.talend.cwm.management.connection.DatabaseContentRetriever;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import orgomg.cwm.foundation.typemapping.TypeSystem;

/**
 * @author scorreia
 * 
 * This class create softwaredeployment classes from a connection.
 */
public class SoftwareSystemBuilder extends CwmBuilder {

    private final TdSoftwareSystem softwareSystem;

    private TypeSystem typeSystem;

    public SoftwareSystemBuilder(Connection conn) throws SQLException {
        super(conn);
        softwareSystem = initializeSoftwareSystem();
    }

    private TdSoftwareSystem initializeSoftwareSystem() throws SQLException {
        TdSoftwareSystem system = DatabaseContentRetriever.getSoftwareSystem(connection);
        this.typeSystem = DatabaseContentRetriever.getTypeSystem(connection);

        // --- add type systems: softwareSystem.getTypespace()
        system.getTypespace().add(typeSystem);

        return system;
    }

    public TdSoftwareSystem getSoftwareSystem() {
        return this.softwareSystem;
    }

    public TypeSystem getTypeSystem() {
        return this.typeSystem;
    }

}
