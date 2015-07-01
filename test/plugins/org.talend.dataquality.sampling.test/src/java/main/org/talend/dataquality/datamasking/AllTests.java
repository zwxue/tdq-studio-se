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
package org.talend.dataquality.datamasking;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * created by jgonzalez on 1 juil. 2015
 * Detailled comment
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ BetweenIndexesKeepTest.class, BetweenIndexesRemoveTest.class, BetweenIndexesReplaceTest.class,
        DateVarianceTest.class, GenerateAccountNumberFormatTest.class, GenerateAccountNumberSimpleTest.class,
        GenerateBetweenDateTest.class, GenerateBetweenIntegerTest.class, GenerateBetweenLongTest.class,
        GenerateBetweenStringTest.class, GenerateCreditCardFormatLongTest.class, GenerateCreditCardFormatStringTest.class,
        GenerateCreditCardLongTest.class, GenerateCreditCardStringTest.class, GenerateFromFileHashIntegerTest.class,
        GenerateFromFileHashLongTest.class, GenerateFromFileHashStringTest.class, GenerateFromFileIntegerTest.class,
        GenerateFromFileLongTest.class, GenerateFromFileStringTest.class, GenerateFromListHashIntegerTest.class,
        GenerateFromListHashLongTest.class, GenerateFromListHashStringTest.class, GenerateFromListIntegerTest.class,
        GenerateFromListLongTest.class, GenerateFromListStringTest.class, GeneratePhoneNumberTest.class,
        KeepFirstAndGenerateIntegerTest.class, KeepFirstAndGenerateLongTest.class, KeepFirstAndGenerateStringTest.class,
        KeepLastAndGenerateIntegerTest.class, KeepLastAndGenerateLongTest.class, KeepLastAndGenerateStringTest.class,
        KeepYearTest.class, MaskAddressTest.class, MaskEmailTest.class, NumericVarianceIntegerTest.class,
        NumericVarianceLongTest.class, RemoveFirstCharsIntegerTest.class, RemoveFirstCharsLongTest.class,
        RemoveFirstCharsStringTest.class, RemoveLastCharsIntegerTest.class, RemoveLastCharsLongTest.class,
        RemoveLastCharsStringTest.class, RepalceAllTest.class, ReplaceCharactersTest.class, ReplaceFirstCharsIntegerTest.class,
        ReplaceFirstCharsLongTest.class, ReplaceFirstCharsStringTest.class, ReplaceLastCharsIntegerTest.class,
        ReplaceLastCharsLongTest.class, ReplaceLastCharsStringTest.class, ReplaceNumericIntegerTest.class,
        ReplaceNumericLongTest.class, ReplaceNumericStringTest.class, ReplaceSsnLongTest.class, ReplaceSsnStringTest.class,
        SetToNullTest.class })
public class AllTests {

}
