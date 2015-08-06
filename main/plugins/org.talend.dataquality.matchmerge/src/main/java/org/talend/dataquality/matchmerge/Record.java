/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.dataquality.matchmerge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Record {

    public static final double MAX_CONFIDENCE = 1.0;

    private final List<Attribute> attributes;

    private final String id;

    private final long timestamp;

    private final String source;

    private String groupId;

    private Set<String> relatedIds = new HashSet<String>();

    private double confidence = MAX_CONFIDENCE;

    public Record(String id, long timestamp, String source) {
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
        this.attributes = new ArrayList<Attribute>();
    }

    public Record(List<Attribute> attributes, String id, long timestamp, String source) {
        this.attributes = attributes;
        this.id = id;
        this.timestamp = timestamp;
        this.source = source;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Set<String> getRelatedIds() {
        return relatedIds;
    }

    public void setRelatedIds(Set<String> relatedIds) {
        this.relatedIds = relatedIds;
    }

    public void clear() {
        attributes.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (id != null ? !id.equals(record.id) : record.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String relatedId : relatedIds) {
            builder.append(relatedId).append(' ');
        }
        return id + " ( " + builder + ")";
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        if (confidence > MAX_CONFIDENCE) {
            throw new IllegalArgumentException("Confidence value '" + confidence + "' is incorrect (>" + MAX_CONFIDENCE + ".");
        }
        this.confidence = confidence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getSource() {
        return source;
    }
}
