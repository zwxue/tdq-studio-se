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
package org.talend.dataquality.matchmerge.mfb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.talend.dataquality.matchmerge.Attribute;
import org.talend.dataquality.matchmerge.Record;

public class ValuesIterator implements Iterator<Record> {

    private final int size;

    private List<Map<String, ValueGenerator>> rcdGenerators = new ArrayList<Map<String, ValueGenerator>>();

    private int currentIndex = 0;

    private long timestamp = 0;

    public ValuesIterator(int size, Map<String, ValueGenerator> generators) {
        this.size = size;
        rcdGenerators.add(generators);
    }

    /**
     * 
     * Record iterators
     * 
     * @param size the record count.
     * @param generators record generator.
     */
    public ValuesIterator(int size, List<Map<String, ValueGenerator>> generators) {
        this.size = size;
        this.rcdGenerators = generators;
    }

    public interface ValueGenerator {

        String newValue();
    }

    @Override
    public boolean hasNext() {
        return currentIndex < size;
    }

    @Override
    public Record next() {
        Vector<Attribute> record = new Vector<Attribute>();
        // Records
        int rcdIdx = currentIndex;
        if (currentIndex >= rcdGenerators.size()) {
            // Keep the compatibility to old behavior (MDM Junit testing only take one record but can run several times
            // , see MFBTest .)
            rcdIdx = 0;

        }
        Map<String, ValueGenerator> recordMap = rcdGenerators.get(rcdIdx);
        // Attributes
        for (Map.Entry<String, ValueGenerator> generator : recordMap.entrySet()) {
            Attribute attribute = new Attribute(generator.getKey());
            attribute.setValue(generator.getValue().newValue());
            record.add(attribute);
        }
        currentIndex++;
        return new Record(record, String.valueOf(currentIndex - 1), timestamp++, "MFB");
    }

    @Override
    public void remove() {
        throw new RuntimeException("Not supported");
    }
}
