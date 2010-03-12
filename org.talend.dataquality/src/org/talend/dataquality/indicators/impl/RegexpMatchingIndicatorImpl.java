/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.i18n.Messages;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Regexp Matching Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class RegexpMatchingIndicatorImpl extends PatternMatchingIndicatorImpl implements RegexpMatchingIndicator {

    private static Logger log = Logger.getLogger(RegexpMatchingIndicatorImpl.class);
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected RegexpMatchingIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.REGEXP_MATCHING_INDICATOR;
    }

    private String regex = null;

    private java.util.regex.Pattern pattern = null;

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#prepare()
     */
    @Override
    public boolean prepare() {
        this.regex = getRegex();
        if (regex == null) {
            return false;
        }
        pattern = java.util.regex.Pattern.compile(regex);
        if (log.isInfoEnabled()) {
            log.info(Messages.getString("Using_regular_expression", this.getName(), regex));
        }
        return super.prepare();
    }

    /**
     * DOC scorreia Comment method "getRegex".
     * 
     * @return
     */
    private String getRegex() {
        if (this.parameters != null) {
            final Domain dataValidDomain = parameters.getDataValidDomain();
            if (dataValidDomain != null) {
                final EList<Pattern> patterns = dataValidDomain.getPatterns();
                for (Pattern p : patterns) {
                    if (p != null) {
                        // MOD yyi 2009-09-29 Feature: 9289
                        String r = DomainHelper.getJavaRegexp(p);
                        if (r == null) { // get regex valid for all kind of database and engine
                            r = DomainHelper.getSQLRegexp(p);
                        }
                        if (r != null) {
                            if (r.startsWith("'") && r.endsWith("'")) {
                                // remove enclosing singles quotes which are used for SQL only (not java)
                                r = r.substring(1, r.length() - 1);
                            }
                            return r;
                        }
                    }
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#finalizeComputation()
     */
    @Override
    public boolean finalizeComputation() {
        if (count != null && matchingValueCount != null) {
            this.setNotMatchingValueCount(count - matchingValueCount);
        }
        return super.finalizeComputation();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        if (data != null) {
            String body = String.valueOf(data);
            Matcher matcher = pattern.matcher(body);
            if (matcher.find()) {
                this.matchingValueCount++;
            }
        }

        boolean ok = super.handle(data);

        return ok;
    }

} // RegexpMatchingIndicatorImpl
