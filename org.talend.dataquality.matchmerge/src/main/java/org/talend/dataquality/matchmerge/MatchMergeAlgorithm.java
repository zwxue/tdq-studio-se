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

import org.talend.dataquality.record.linkage.record.IRecordMatcher;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public interface MatchMergeAlgorithm extends IRecordMatcher {

    List<Record> execute(Iterator<Record> sourceRecords);

}
