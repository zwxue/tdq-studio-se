package org.talend.datascience.common.recordlinkage;

import org.talend.dataquality.record.linkage.constant.AttributeMatcherType;

/**
 * Use to match & merge values between different blocks.
 */
public class PostMerge {

    AttributeMatcherType matcher;

    float threshold;

    public PostMerge(AttributeMatcherType matcher, float threshold) {
        this.matcher = matcher;
        this.threshold = threshold;
    }
}
