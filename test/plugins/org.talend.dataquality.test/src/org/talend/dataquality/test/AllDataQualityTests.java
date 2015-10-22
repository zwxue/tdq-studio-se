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
package org.talend.dataquality.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.talend.commons.MapDB.utils.ColumnSetDBMapTest;
import org.talend.commons.MapDB.utils.DBMapTest;
import org.talend.commons.MapDB.utils.DBSetTest;
import org.talend.dataquality.helpers.IndicatorHelperTest;
import org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImplTest;
import org.talend.dataquality.indicators.columnset.impl.RecordMatchingIndicatorImplTest;
import org.talend.dataquality.indicators.impl.BenfordLawFrequencyIndicatorImplTest;
import org.talend.dataquality.indicators.impl.DuplicateCountIndicatorImplTest;
import org.talend.dataquality.indicators.impl.FormatFreqPieIndicatorImplTest;
import org.talend.dataquality.indicators.impl.FrequencyIndicatorImplTest;
import org.talend.dataquality.indicators.impl.MeanIndicatorImplTest;
import org.talend.dataquality.indicators.impl.ModeIndicatorImplTest;
import org.talend.dataquality.indicators.impl.PatternFreqIndicatorImplTest;
import org.talend.dataquality.indicators.impl.RegexpMatchingIndicatorImplTest;
import org.talend.dataquality.indicators.impl.WellFormE164PhoneCountIndicatorImplTest;
import org.talend.dataquality.indicators.impl.WellFormIntePhoneCountIndicatorImplTest;
import org.talend.dataquality.indicators.impl.WellFormNationalPhoneCountIndicatorImplTest;
import org.talend.dataquality.indicators.util.FullwidthLatinLowercasedLettersTest;
import org.talend.dataquality.indicators.util.FullwidthLatinNumbersTest;
import org.talend.dataquality.indicators.util.FullwidthLatinUppercasedLettersTest;
import org.talend.dataquality.indicators.util.HangulTest;
import org.talend.dataquality.indicators.util.HiraganaSmallTest;
import org.talend.dataquality.indicators.util.HiraganaTest;
import org.talend.dataquality.indicators.util.KanjiTest;
import org.talend.dataquality.indicators.util.KatakanaSmallTest;
import org.talend.dataquality.indicators.util.KatakanaTest;
import org.talend.dataquality.properties.impl.TDQJrxmlItemImplTest;
import org.talend.dataquality.properties.impl.TDQSourceFileItemImplTest;

/**
 * DOC yyin class global comment. Detailled comment
 */
@RunWith(Suite.class)
@SuiteClasses({ BenfordLawFrequencyIndicatorImplTest.class, DuplicateCountIndicatorImplTest.class,
        FormatFreqPieIndicatorImplTest.class, MeanIndicatorImplTest.class, ModeIndicatorImplTest.class,
        RegexpMatchingIndicatorImplTest.class, WellFormE164PhoneCountIndicatorImplTest.class,
        WellFormIntePhoneCountIndicatorImplTest.class, WellFormNationalPhoneCountIndicatorImplTest.class,
        IndicatorHelperTest.class, ColumnSetMultiValueIndicatorImplTest.class, RecordMatchingIndicatorImplTest.class,
        TDQJrxmlItemImplTest.class, TDQSourceFileItemImplTest.class, DBMapTest.class, DBSetTest.class, ColumnSetDBMapTest.class,
        FrequencyIndicatorImplTest.class, KatakanaTest.class, KatakanaSmallTest.class, KanjiTest.class, HiraganaTest.class,
        HiraganaSmallTest.class, HangulTest.class, FullwidthLatinUppercasedLettersTest.class, FullwidthLatinNumbersTest.class,
        FullwidthLatinLowercasedLettersTest.class, PatternFreqIndicatorImplTest.class })
public class AllDataQualityTests {

}
