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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.talend.dataquality.datamasking.CreditCardGenerator.CreditCardType;
import org.talend.dataquality.duplicating.DateChanger;
import org.talend.dataquality.duplicating.RandomWrapper;

public class FunctionApplier {

    /**
     * created by jgonzalez on 9 juin 2015 This enum holds all the diiferent functions that can be applied.
     *
     */
    public enum Function {
        SET_TO_NULL,
        KEEP_YEAR,
        DATE_VARIANCE,
        NUMERIC_VARIANCE,
        MASK_EMAIL,
        MASK_ADDRESS,
        GENERATE_CREDIT_CARD,
        GENERATE_CREDIT_CARD_FORMAT,
        GENERATE_ACCOUNT_NUMBER,
        GENERATE_ACCOUNT_NUMBER_FORMAT,
        GENERATE_PHONE_NUMBER,
        GENERATE_BETWEEN,
        GENERATE_FROM_LIST,
        GENERATE_FROM_FILE,
        HASH_GENERATE_LIST,
        HASH_GENERATE_FILE,
        REPLACE_ALL,
        REPLACE_NUMERIC,
        REPLACE_CHARACTERS,
        REPLACE_SSN,
        REPLACE_BETWEEN_INDEXES,
        KEEP_BETWEEN_INDEXES,
        REMOVE_BETWEEN_INDEXES,
        REMOVE_FIRST_CHARS,
        REMOVE_LAST_CHARS,
        REPLACE_FIRST_CHARS,
        REPLACE_LAST_CHARS,
        KEEP_FIRST_AND_GENERATE,
        KEEP_LAST_AND_GENERATE,
        GENERATE_UUID,
        GENERATE_SEQUENCE
    };

    private DateChanger dateChanger = new DateChanger();

    private String EMPTY_STRING = ""; //$NON-NLS-1$

    private String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; //$NON-NLS-1$

    private String LOWER = "abcdefghijklmnopqrstuvwxyz"; //$NON-NLS-1$

    private String[] keys = new String[109];

    private List<String> StringTokens = new ArrayList<>();

    private RandomWrapper rnd = new RandomWrapper();

    private int seq = 0;

    private Integer integerParam = 0;

    private String[] parameters = new String[1];

    private boolean first = true;

    private boolean keepNull = false;

    private Scanner in = null;

    private Calendar c = null;

    /**
     * DOC jgonzalez Comment method "setSeq". This function is used for the GENERATE_SEQUENCE function.
     * 
     * @param i The element that starts the sequence.
     */
    public void setSeq(int i) {
        if (first) {
            seq = i;
            first = false;
        }
    }

    /**
     * DOC jgonzalez Comment method "setSeed". This function sets the seed used for all calls to random functions.
     * 
     * @param seed The seed itself.
     */
    public void setSeed(long seed) {
        dateChanger.setSeed(seed);
        rnd = new RandomWrapper(seed);
    }

    /**
     * DOC jgonzalez Comment method "setKeepNull". This function sets a boolean used to keep null values.
     * 
     * @param keep The value of the boolean.
     */
    public void setKeepNull(boolean keep) {
        this.keepNull = keep;
    }

    /**
     * DOC jgonzalez Comment method "initialize". This function is called at the beginning of the job and sets different
     * settings : getting the parameter, the calendar, the key words for the MARK_ADDRESSES function and the Scanner.
     * 
     * @param extraParameter The parameter we try to parse.
     */
    public void initialize(String extraParameter) {
        String[] addressKeys = {
                "Rue", "rue", "r.", "strasse", "Strasse", "Street", "street", "St.", "St", "Straße", "Strada", "Rua", "Calle", "Ave.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$
                "avenue", "Av.", "Allee", "allee", "allée", "Avenue", "Avenida", "Bvd.", "Bd.", "Boulevard", "boulevard", "Blv.", "Viale", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$
                "Avenida", "Bulevar", "Route", "route", "road", "Road", "Rd.", "Chemin", "Way", "Cour", "Court", "Ct.", "Place", "place", "Pl.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$
                "Square", "Impasse", "Allée", "Driveway", "Auffahrt", "Viale", "Esplanade", "Esplanade", "Promenade", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
                "Lungomare", "Esplanada", "Esplanada", "Faubourg", "faubourg", "Suburb", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                "Vorort", "Periferia", "Subúrbio", "Suburbio", "Via", "Via", "industrial", "area", "zone", "industrielle", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$
                "Périphérique", "Peripheral", "Voie", "voie", "Track", "Gleis", "Carreggiata", "Caminho", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
                "Pista", "Forum", "STREET", "RUE", "ST.", "AVENUE", "BOULEVARD", "BLV.", "BD", "ROAD", "ROUTE", "RD.", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
                "RTE", "WAY", "CHEMIN", "COURT", "CT.", "SQUARE", "DRIVEWAY", "ALLEE", "DR.", "ESPLANADE", "SUBURB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
                "BANLIEUE", "VIA", "PERIPHERAL", "PERIPHERIQUE", "TRACK", "VOIE", "FORUM", "INDUSTRIAL", "AREA", "ZONE", "INDUSTRIELLE" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$

        System.arraycopy(addressKeys, 0, keys, 0, addressKeys.length);

        if (extraParameter != null) {
            try {
                parameters = extraParameter.split(","); //$NON-NLS-1$
                integerParam = parameters.length == 1 ? Integer.parseInt(parameters[0]) : 0;
            } catch (NumberFormatException e) {
                // We do nothing here because parameters[] is already set.
            }
        }

        StringTokens.clear();
        try {
            in = new Scanner(new FileReader(extraParameter));
            while (in.hasNext()) {
                StringTokens.add(in.next());
            }
            in.close();
        } catch (FileNotFoundException e) {
            // We do nothing here because in is already set.
        }

        c = Calendar.getInstance();
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is a date.
     * 
     * @param date The input date to be masked.
     * @param function The function we want to apply.
     * @return A new date modified by the function.
     */
    public Date generateMaskedRow(Date date, Function function) {
        if (function == Function.SET_TO_NULL || date == null && keepNull) {
            return null;
        }
        Date newDate = new Date(System.currentTimeMillis());
        switch (function) {
        case DATE_VARIANCE:
            if (date != null) {
                if (integerParam < 0) {
                    integerParam *= -1;
                } else if (integerParam == 0) {
                    integerParam = 31;
                }
                newDate = dateChanger.dateVariance(date, integerParam);
            }
            break;
        case GENERATE_BETWEEN:
            if (parameters.length == 2) {
                newDate = dateChanger.generateDateBetween(parameters[0], parameters[1], rnd);
            }
            break;
        case KEEP_YEAR:
            if (date != null) {
                c.setTime(date);
            } else {
                c.setTime(newDate);
            }
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.set(Calendar.MONTH, Calendar.JANUARY);
            newDate = c.getTime();
            break;
        default:
            return new Date(System.currentTimeMillis());
        }
        return newDate;
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is a string.
     * 
     * @param str The input string we want to mask.
     * @param function The function we want to apply.
     * @return A new string, modified by the function.
     */
    public String generateMaskedRow(String str, Function function) {
        if (function == Function.SET_TO_NULL || (str == null || EMPTY_STRING.equals(str)) && keepNull
                && function != Function.GENERATE_SEQUENCE) {
            return null;
        }
        StringBuilder sb = new StringBuilder(EMPTY_STRING);
        switch (function) {
        case MASK_EMAIL:
            if (str != null && !EMPTY_STRING.equals(str)) {
                int count = str.lastIndexOf('@');
                if (count == -1) {
                    count = str.length();
                }
                for (int i = 0; i < count; ++i) {
                    sb.append("X"); //$NON-NLS-1$
                }
                sb.append(str.substring(count, str.length()));
            }
            break;
        case MASK_ADDRESS:
            if (str != null && !EMPTY_STRING.equals(str)) {
                String[] address = str.split(",| "); //$NON-NLS-1$
                for (String tmp : address) {
                    if (Arrays.asList(keys).contains(tmp)) {
                        sb.append(tmp + " "); //$NON-NLS-1$
                    } else {
                        for (int i = 0; i < tmp.length(); ++i) {
                            if (Character.isDigit(tmp.charAt(i))) {
                                sb.append(Character.forDigit(rnd.nextInt(9), 10));
                            } else {
                                sb.append("X"); //$NON-NLS-1$
                            }
                        }
                        sb.append(" "); //$NON-NLS-1$
                    }
                }
            }
            break;
        case GENERATE_CREDIT_CARD:
            CreditCardGenerator ccg = new CreditCardGenerator(rnd);
            CreditCardType cct = ccg.chooseCreditCardType();
            sb = new StringBuilder(ccg.generateCreditCard(cct).toString());
            break;
        case GENERATE_CREDIT_CARD_FORMAT:
            boolean keep_format = ("true").equals(parameters[0]); //$NON-NLS-1$ 
            CreditCardGenerator ccgf = new CreditCardGenerator(rnd);
            CreditCardType cct_format = null;
            if (str == null || EMPTY_STRING.equals(str)) {
                cct_format = ccgf.chooseCreditCardType();
                sb = new StringBuilder(ccgf.generateCreditCard(cct_format).toString());
            } else {
                try {
                    cct_format = ccgf.getCreditCardType(Long.parseLong(str.replaceAll("\\s+", EMPTY_STRING))); //$NON-NLS-1$ 
                } catch (NumberFormatException e) {
                    cct_format = ccgf.chooseCreditCardType();
                    sb = new StringBuilder(ccgf.generateCreditCard(cct_format).toString());
                    break;
                }
                if (cct_format != null) {
                    sb = new StringBuilder(ccgf.generateCreditCardFormat(cct_format, str, keep_format));
                    break;
                } else {
                    cct_format = ccgf.chooseCreditCardType();
                    sb = new StringBuilder(ccgf.generateCreditCard(cct_format).toString());
                }
            }
            break;
        case GENERATE_ACCOUNT_NUMBER:
            AccountNumberGenerator ang = new AccountNumberGenerator(rnd);
            String accountNumber = ang.generateIban();
            sb = new StringBuilder(accountNumber);
            break;
        case GENERATE_ACCOUNT_NUMBER_FORMAT:
            boolean keepFormat = ("true").equals(parameters[0]); //$NON-NLS-1$
            AccountNumberGenerator angf = new AccountNumberGenerator(rnd);
            String accountNumberFormat = EMPTY_STRING;
            if (str != null && str.length() > 9 && !EMPTY_STRING.equals(str)) {
                try {
                    accountNumberFormat = angf.generateIban(str, keepFormat);
                } catch (NumberFormatException e) {
                    accountNumberFormat = angf.generateIban();
                }
            } else {
                accountNumberFormat = angf.generateIban();
            }
            sb = new StringBuilder(accountNumberFormat);
            break;
        case GENERATE_PHONE_NUMBER:
            PhoneNumberGenerator png = new PhoneNumberGenerator(rnd);
            String phoneNumber = png.generatePhoneNumber();
            sb = new StringBuilder(phoneNumber);
            break;
        case REPLACE_ALL:
            if (str != null && !EMPTY_STRING.equals(str) && parameters[0].matches("[0-9]|[a-zA-Z]")) { //$NON-NLS-1$
                sb = new StringBuilder(str.replaceAll(".", parameters[0])); //$NON-NLS-1$
            }
            break;
        case REPLACE_NUMERIC:
            if (str != null && !EMPTY_STRING.equals(str) && parameters[0].matches("[0-9]|[a-zA-Z]| ")) { //$NON-NLS-1$
                if ((" ").equals(parameters[0])) { //$NON-NLS-1$
                    sb = new StringBuilder(str.replaceAll("\\d", parameters[0]).replace(" ", EMPTY_STRING)); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                    sb = new StringBuilder(str.replaceAll("\\d", parameters[0])); //$NON-NLS-1$
                }
            }
            break;
        case REPLACE_CHARACTERS:
            if (str != null && !EMPTY_STRING.equals(str) && parameters[0].matches("[0-9]|[a-zA-Z]| ")) { //$NON-NLS-1$
                if ((" ").equals(parameters[0])) { //$NON-NLS-1$
                    sb = new StringBuilder(str.replaceAll("[a-zA-Z]", parameters[0]).replace(" ", EMPTY_STRING)); //$NON-NLS-1$ //$NON-NLS-2$   
                } else {
                    sb = new StringBuilder(str.replaceAll("[a-zA-Z]", parameters[0])); //$NON-NLS-1$
                }
            }
            break;
        case REPLACE_SSN:
            if (str != null && !EMPTY_STRING.equals(str) && parameters[0].matches("[0-9]|[a-zA-Z]")) { //$NON-NLS-1$
                int digits_to_keep = 0;
                String str_nospaces = str.replaceAll("\\s+", EMPTY_STRING); //$NON-NLS-1$
                if (str_nospaces.replaceAll("\\D", EMPTY_STRING).length() == 9) {//$NON-NLS-1$
                    digits_to_keep = 4;
                } else if (str_nospaces.replaceAll("\\D", EMPTY_STRING).length() == 15) { //$NON-NLS-1$
                    digits_to_keep = 5;
                }
                String res = str_nospaces.substring(0, str_nospaces.length() - digits_to_keep).replaceAll("[0-9]", parameters[0]); //$NON-NLS-1$ 
                res = res + str_nospaces.substring(str_nospaces.length() - digits_to_keep, str_nospaces.length());
                sb = new StringBuilder(res);
            }
            break;
        case GENERATE_BETWEEN:
            if (parameters.length == 2) {
                int a = 0;
                int b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int min = (a < b) ? a : b;
                int max = (a < b) ? b : a;
                sb = new StringBuilder(String.valueOf(rnd.nextInt((max - min) + 1) + min));
            }
            break;
        case GENERATE_FROM_LIST:
            if (parameters.length > 0) {
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].trim();
                    parameters[i] = tmp;
                }
                sb = new StringBuilder(parameters[rnd.nextInt(parameters.length)]);
            }
            break;
        case GENERATE_FROM_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    sb = new StringBuilder(StringTokens.get(rnd.nextInt(StringTokens.size())));
                }
            }
            break;
        case HASH_GENERATE_LIST:
            if (parameters.length > 0) {
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].trim();
                    parameters[i] = tmp;
                }
                if (str == null) {
                    sb = new StringBuilder(parameters[rnd.nextInt(parameters.length)]);
                } else {
                    sb = new StringBuilder(parameters[(Math.abs(str.hashCode()) % parameters.length)]);
                }
            }
            break;
        case HASH_GENERATE_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    if (str == null) {
                        sb = new StringBuilder(StringTokens.get(rnd.nextInt(StringTokens.size())));
                    } else {
                        sb = new StringBuilder(StringTokens.get(Math.abs(str.hashCode()) % StringTokens.size()));
                    }
                }
            }
            break;
        case KEEP_BETWEEN_INDEXES:
            if (str != null && !EMPTY_STRING.equals(str) && parameters.length == 2) {
                int a = 0, b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int begin = (a < b) ? a : b;
                int end = (a > b) ? a : b;
                if (begin < 1) {
                    begin = 1;
                }
                if (end > str.length()) {
                    end = str.length();
                }
                sb = new StringBuilder(str.substring(begin - 1, end));
            }
            break;
        case REMOVE_BETWEEN_INDEXES:
            if (str != null && !EMPTY_STRING.equals(str) && parameters.length == 2) {
                int a = 0, b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    sb = new StringBuilder(EMPTY_STRING);
                    break;
                }
                int begin = (a < b) ? a : b;
                int end = (a > b) ? a : b;
                if (begin < 1) {
                    begin = 1;
                }
                if (end > str.length()) {
                    end = str.length();
                }
                sb = new StringBuilder(str.substring(0, begin - 1) + str.substring(end, str.length()));
            }
            break;
        case REPLACE_BETWEEN_INDEXES:
            if (str != null && !EMPTY_STRING.equals(str) && (parameters.length == 2 || parameters.length == 3)) {
                int a = 0, b = 0;
                String s = null;
                boolean isThird = true;
                char ch = ' ';
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int begin = (a < b) ? a : b;
                int end = (a > b) ? a : b;
                try {
                    s = parameters[2].trim();
                    if (!s.matches("[0-9]|[a-zA-Z]")) { //$NON-NLS-1$                                                                                                               
                        isThird = false;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    s = EMPTY_STRING;
                    isThird = false;
                }
                if (begin < 1) {
                    begin = 1;
                }
                if (end > str.length()) {
                    end = str.length();
                }
                sb = new StringBuilder(str);
                if (!isThird) {
                    for (int i = begin - 1; i < end; ++i) {
                        if (Character.isDigit(str.charAt(i))) {
                            sb.setCharAt(i, Character.forDigit(rnd.nextInt(9), 10));
                        } else if (Character.isUpperCase(str.charAt(i))) {
                            sb.setCharAt(i, UPPER.charAt(rnd.nextInt(26)));
                        } else if (Character.isLowerCase(str.charAt(i))) {
                            sb.setCharAt(i, LOWER.charAt(rnd.nextInt(26)));
                        } else {
                            sb.setCharAt(i, str.charAt(i));
                        }
                    }
                } else {
                    ch = s.toCharArray()[0];
                    for (int i = begin - 1; i < end; ++i) {
                        sb.setCharAt(i, ch);
                    }
                }

            }
            break;
        case REMOVE_FIRST_CHARS:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                if (integerParam > str.length()) {
                    integerParam = str.length();
                }
                sb = new StringBuilder(str.substring(integerParam));
            }
            break;
        case REMOVE_LAST_CHARS:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                if (integerParam > str.length()) {
                    integerParam = str.length();
                }
                sb = new StringBuilder(str.substring(0, str.length() - integerParam));
            }
            break;
        case REPLACE_FIRST_CHARS:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                if (integerParam > str.length()) {
                    integerParam = str.length();
                }
                sb = new StringBuilder(str);
                StringBuilder repl = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < integerParam; ++i) {
                    if (Character.isDigit(str.charAt(i))) {
                        repl.append(rnd.nextInt(9));
                    } else if (Character.isUpperCase(str.charAt(i))) {
                        repl.append(UPPER.charAt(rnd.nextInt(26)));
                    } else if (Character.isLowerCase(str.charAt(i))) {
                        repl.append(LOWER.charAt(rnd.nextInt(26)));
                    } else {
                        repl.append(str.charAt(i));
                    }
                }
                sb.replace(0, integerParam, repl.toString());
            }
            break;
        case REPLACE_LAST_CHARS:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                if (integerParam > str.length()) {
                    integerParam = str.length();
                }
                sb = new StringBuilder(str);
                StringBuilder repla = new StringBuilder(EMPTY_STRING);
                for (int i = sb.length() - integerParam; i < sb.length(); ++i) {
                    if (Character.isDigit(str.charAt(i))) {
                        repla.append(rnd.nextInt(9));
                    } else if (Character.isUpperCase(str.charAt(i))) {
                        repla.append(UPPER.charAt(rnd.nextInt(26)));
                    } else if (Character.isLowerCase(str.charAt(i))) {
                        repla.append(LOWER.charAt(rnd.nextInt(26)));
                    } else {
                        repla.append(str.charAt(i));
                    }
                }
                sb.replace(str.length() - integerParam, str.length(), repla.toString());
            }
            break;
        case KEEP_FIRST_AND_GENERATE:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                String s = str.trim();
                if (integerParam < s.length()) {
                    for (int i = 0; i < integerParam; ++i) {
                        sb.append(s.charAt(i));
                        if (!Character.isDigit(s.charAt(i))) {
                            integerParam++;
                        }
                    }
                    for (int i = integerParam; i < s.length(); ++i) {
                        if (Character.isDigit(s.charAt(i))) {
                            sb.append(rnd.nextInt(9));
                        } else {
                            sb.append(s.charAt(i));
                        }
                    }
                }
            }
            break;
        case KEEP_LAST_AND_GENERATE:
            if (str != null && !EMPTY_STRING.equals(str) && integerParam > 0) {
                String s = str.trim();
                if (integerParam < s.length()) {
                    StringBuilder end = new StringBuilder(EMPTY_STRING);
                    for (int i = s.length() - 1; i >= s.length() - integerParam; --i) {
                        end.append(s.charAt(i));
                        if (!Character.isDigit(s.charAt(i))) {
                            integerParam++;
                        }
                    }
                    for (int i = 0; i < s.length() - integerParam; ++i) {
                        if (Character.isDigit(s.charAt(i))) {
                            sb.append(rnd.nextInt(9));
                        } else {
                            sb.append(s.charAt(i));
                        }
                    }
                    sb.append(end.reverse());
                }
            }
            break;
        case GENERATE_UUID:
            sb = new StringBuilder(UUID.randomUUID().toString());
            break;
        case GENERATE_SEQUENCE:
            sb.append(seq++);
            break;
        default:
            return sb.toString();
        }
        return sb.toString();
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is a double.
     * 
     * @param valueIn The input double we want to mask.
     * @param function The function we want to apply.
     * @return A new double modified after applying the function.
     */
    public Double generateMaskedRow(Double valueIn, Function function) {
        if (function == Function.SET_TO_NULL || valueIn == null && keepNull && function != Function.GENERATE_SEQUENCE) {
            return null;
        }
        Double finalValue = 0.0;
        switch (function) {
        case NUMERIC_VARIANCE:
            if (valueIn != null) {
                if (integerParam == 0) {
                    integerParam = 10;
                } else if (integerParam <= 0) {
                    integerParam *= -1;
                }
                int rate = 0;
                do {
                    rate = rnd.nextInt(2 * integerParam) - integerParam;
                } while (rate == 0);
                Double value = Double.parseDouble(valueIn.toString());
                value *= ((double) rate + 100) / 100;
                finalValue = new Double(value);
            }
            break;
        case REPLACE_NUMERIC:
            if (valueIn != null) {
                if (integerParam < 0 || integerParam > 9) {
                    integerParam = 0;
                }
                String str = valueIn.toString();
                String res = str.replaceAll("\\d", integerParam.toString()); //$NON-NLS-1$
                finalValue = Double.valueOf(res);
            }
            break;
        case GENERATE_BETWEEN:
            if (parameters.length == 2) {
                int a = 0;
                int b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int min = (a < b) ? a : b;
                int max = (a < b) ? b : a;
                finalValue = (double) rnd.nextInt((max - min) + 1) + min;
            }
            break;
        case GENERATE_SEQUENCE:
            finalValue = Double.valueOf(seq++);
            break;
        default:
            return finalValue;
        }
        return finalValue;
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is a float.
     * 
     * @param valueIn The input float we want to mask.
     * @param function The function we want to apply.
     * @return A new float modified after applying the function.
     */
    public Float generateMaskedRow(Float valueIn, Function function) {
        if (function == Function.SET_TO_NULL || valueIn == null && keepNull && function != Function.GENERATE_SEQUENCE) {
            return null;
        }
        Float finalValue = 0f;

        if (valueIn != null) {
            double d = Double.valueOf(valueIn);
            d = generateMaskedRow(d, function);
            finalValue = (float) d;
        }

        return finalValue;
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is a long.
     * 
     * @param valueIn The input long we want to mask.
     * @param function The function we want to apply.
     * @return A new long modified after applying the function.
     */
    public Long generateMaskedRow(Long valueIn, Function function) {
        if (function == Function.SET_TO_NULL || valueIn == null && keepNull && function != Function.GENERATE_SEQUENCE) {
            return null;
        }
        Long finalValue = 0L;
        switch (function) {
        case NUMERIC_VARIANCE:
            if (valueIn != null) {
                if (integerParam < 0) {
                    integerParam *= -1;
                } else if (integerParam == 0) {
                    integerParam = 10;
                }
                int rate = 0;
                do {
                    rate = rnd.nextInt(2 * integerParam) - integerParam;
                } while (rate == 0);
                Double value = Double.parseDouble(valueIn.toString());
                value *= ((double) rate + 100) / 100;
                finalValue = new Long(Math.round(value));
            }
            break;
        case GENERATE_CREDIT_CARD:
            CreditCardGenerator ccg = new CreditCardGenerator(rnd);
            CreditCardType cct = ccg.chooseCreditCardType();
            finalValue = ccg.generateCreditCard(cct);
            break;
        case GENERATE_CREDIT_CARD_FORMAT:
            CreditCardGenerator ccgf = new CreditCardGenerator(rnd);
            CreditCardType cct_format = null;
            if (valueIn == null) {
                cct_format = ccgf.chooseCreditCardType();
                finalValue = ccgf.generateCreditCard(cct_format);
                break;
            } else {
                cct_format = ccgf.getCreditCardType(valueIn);
                if (cct_format == null) {
                    cct_format = ccgf.chooseCreditCardType();
                    finalValue = ccgf.generateCreditCard(cct_format);
                } else {
                    finalValue = ccgf.generateCreditCardFormat(cct_format, valueIn);
                }
            }
            break;
        case REPLACE_NUMERIC:
            if (valueIn != null && integerParam >= 0 && integerParam <= 9) {
                String str = valueIn.toString();
                String res = str.replaceAll("\\d", integerParam.toString()); //$NON-NLS-1$
                finalValue = Long.valueOf(res);
            }
            break;
        case GENERATE_BETWEEN:
            if (parameters.length == 2) {
                int a = 0;
                int b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int min = (a < b) ? a : b;
                int max = (a < b) ? b : a;
                finalValue = (long) rnd.nextInt((max - min) + 1) + min;
            }
            break;
        case REPLACE_SSN:
            if (valueIn != null && integerParam <= 9 && integerParam >= 0) {
                int digits_to_keep = 0;
                String str = valueIn.toString();
                if (str.length() == 9) {
                    digits_to_keep = 4;
                } else if (str.length() == 15) {
                    digits_to_keep = 5;
                }
                String res_ssn = str.substring(0, str.length() - digits_to_keep)
                        .replaceAll("[0-9]", String.valueOf(integerParam)); //$NON-NLS-1$ 
                res_ssn = res_ssn + str.substring(str.length() - digits_to_keep, str.length());
                finalValue = Long.parseLong(res_ssn);
            }
            break;
        case GENERATE_FROM_LIST:
            if (parameters.length > 0) {
                long[] parametersI = new long[parameters.length];
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].replaceAll("\\s+", EMPTY_STRING); //$NON-NLS-1$
                    try {
                        parametersI[i] = Long.parseLong(tmp);
                    } catch (NumberFormatException e) {
                        finalValue = 0L;
                        break;
                    }
                }
                finalValue = parametersI[rnd.nextInt(parametersI.length)];
            }
            break;
        case GENERATE_FROM_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    try {
                        finalValue = Long.parseLong(StringTokens.get(rnd.nextInt(StringTokens.size())));
                    } catch (NumberFormatException e) {
                        // Do nothing here.
                    }
                }
            }
            break;
        case HASH_GENERATE_LIST:
            if (parameters.length > 0) {
                int[] parametersIh = new int[parameters.length];
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].replaceAll("\\s+", EMPTY_STRING); //$NON-NLS-1$
                    try {
                        parametersIh[i] = Integer.parseInt(tmp);
                    } catch (NumberFormatException e) {
                        break;
                    }
                }
                if (valueIn == null) {
                    finalValue = (long) parametersIh[rnd.nextInt(parametersIh.length)];
                } else {
                    finalValue = (long) parametersIh[Math.abs(valueIn.hashCode()) % parametersIh.length];
                }
            }
            break;
        case HASH_GENERATE_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    if (valueIn == null) {
                        finalValue = Long.parseLong(StringTokens.get(rnd.nextInt(StringTokens.size())));
                    } else {
                        finalValue = Long.parseLong(StringTokens.get(Math.abs(valueIn.hashCode()) % StringTokens.size()));
                    }
                }
            }
            break;
        case REMOVE_FIRST_CHARS:
            if (valueIn != null && (int) Math.log10(valueIn) + 1 > integerParam && integerParam > 0) {
                StringBuilder sb = new StringBuilder(valueIn.toString().substring(integerParam));
                finalValue = Long.parseLong(sb.toString());
            }
            break;
        case REMOVE_LAST_CHARS:
            if (valueIn != null) {
                Double extraP = Double.valueOf(integerParam.toString());
                if ((int) Math.log10(valueIn) + 1 > extraP && extraP > 0) {
                    finalValue = valueIn / (long) Math.pow(10.0, extraP);
                }
            }
            break;
        case REPLACE_FIRST_CHARS:
            if (valueIn != null && integerParam > 0) {
                if ((int) Math.log10(valueIn) + 1 < integerParam) {
                    integerParam = (int) Math.log10(valueIn) + 1;
                }
                StringBuilder sbu = new StringBuilder(valueIn.toString());
                StringBuilder remp = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < integerParam; ++i) {
                    remp.append(rnd.nextInt(9));
                }
                sbu.replace(0, integerParam, remp.toString());
                finalValue = Long.parseLong(sbu.toString());
            }
            break;
        case REPLACE_LAST_CHARS:
            if (valueIn != null && integerParam > 0) {
                if ((int) Math.log10(valueIn) + 1 < integerParam) {
                    integerParam = (int) Math.log10(valueIn) + 1;
                }
                StringBuilder sbui = new StringBuilder(valueIn.toString());
                StringBuilder rempl = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < integerParam; ++i) {
                    rempl.append(rnd.nextInt(9));
                }
                sbui.replace(sbui.length() - integerParam, sbui.length(), rempl.toString());
                finalValue = Long.parseLong(sbui.toString());
            }
            break;
        case KEEP_FIRST_AND_GENERATE:
            if (valueIn != null && integerParam > 0) {
                if ((int) Math.log10(valueIn) + 1 < integerParam) {
                    integerParam = (int) Math.log10(valueIn) + 1;
                }
                StringBuilder val = new StringBuilder(valueIn.toString().substring(0, integerParam));
                for (int i = integerParam; i < valueIn.toString().length(); ++i) {
                    val.append(rnd.nextInt(9));
                }
                finalValue = Long.parseLong(val.toString());
            }
            break;
        case KEEP_LAST_AND_GENERATE:
            if (valueIn != null && integerParam > 0 && (int) Math.log10(valueIn) + 1 > integerParam) {
                StringBuilder val = new StringBuilder(EMPTY_STRING);
                for (int i = 0; i < valueIn.toString().length() - integerParam; ++i) {
                    val.append(rnd.nextInt(9));
                }
                val.append(valueIn.toString().substring(valueIn.toString().length() - integerParam, valueIn.toString().length()));
                finalValue = Long.parseLong(val.toString());
            }
            break;
        case GENERATE_SEQUENCE:
            finalValue = (long) seq++;
            break;
        default:
            finalValue = 0L;
        }
        return finalValue;
    }

    /**
     * DOC jgonzalez Comment method "generateMaskedRow". This function is called to generate a new row, when the input
     * parameter is an integer.
     * 
     * @param valueIn The input integer we want to mask.
     * @param function The function we want to apply.
     * @return A new integer modified after applying the function.
     */
    public Integer generateMaskedRow(Integer valueIn, Function function) {
        if (function == Function.SET_TO_NULL || valueIn == null && keepNull && function != Function.GENERATE_SEQUENCE) {
            return null;
        }
        Integer finalValue = 0;
        switch (function) {
        case NUMERIC_VARIANCE:
            if (valueIn != null) {
                if (integerParam < 0) {
                    integerParam *= -1;
                } else if (integerParam == 0) {
                    integerParam = 10;
                }
                int rate = 0;
                do {
                    rate = rnd.nextInt(2 * integerParam) - integerParam;
                } while (rate == 0);
                Float value = Float.parseFloat(valueIn.toString());
                value *= ((float) rate + 100) / 100;
                finalValue = new Integer(Math.round(value));
            }
            break;
        case REPLACE_NUMERIC:
            if (valueIn != null) {
                if (integerParam < 0 || integerParam > 9) {
                    integerParam = 0;
                }
                String str = valueIn.toString();
                String res = str.replaceAll("\\d", integerParam.toString()); //$NON-NLS-1$
                finalValue = Integer.valueOf(res);
            }
            break;
        case GENERATE_BETWEEN:
            if (parameters.length == 2) {
                int a = 0;
                int b = 0;
                try {
                    a = Integer.valueOf(parameters[0].trim());
                    b = Integer.valueOf(parameters[1].trim());
                } catch (NumberFormatException e) {
                    break;
                }
                int min = (a < b) ? a : b;
                int max = (a < b) ? b : a;
                finalValue = rnd.nextInt((max - min) + 1) + min;
            }
            break;
        case GENERATE_FROM_LIST:
            if (parameters.length > 0) {
                int[] parametersI = new int[parameters.length];
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].trim();
                    try {
                        parametersI[i] = Integer.parseInt(tmp);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                finalValue = parametersI[rnd.nextInt(parametersI.length)];
            }
            break;
        case GENERATE_FROM_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    try {
                        finalValue = Integer.parseInt(StringTokens.get(rnd.nextInt(StringTokens.size())));
                    } catch (NumberFormatException e) {
                        // Do nothing here.
                    }
                }
            }
            break;
        case HASH_GENERATE_LIST:
            if (parameters.length > 0) {
                int[] parametersIh = new int[parameters.length];
                for (int i = 0; i < parameters.length; ++i) {
                    String tmp = parameters[i].replaceAll("\\s+", EMPTY_STRING); //$NON-NLS-1$
                    try {
                        parametersIh[i] = Integer.parseInt(tmp);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                }
                if (valueIn == null) {
                    finalValue = parametersIh[rnd.nextInt(parametersIh.length)];
                } else {
                    finalValue = parametersIh[Math.abs(valueIn.hashCode()) % parametersIh.length];
                }
            }
            break;
        case HASH_GENERATE_FILE:
            if (in != null) {
                if (StringTokens.size() > 0) {
                    if (valueIn == null) {
                        finalValue = Integer.parseInt(StringTokens.get(rnd.nextInt(StringTokens.size())));
                    } else {
                        finalValue = Integer.parseInt(StringTokens.get(Math.abs(valueIn.hashCode()) % StringTokens.size()));
                    }
                }
            }
            break;
        case REMOVE_FIRST_CHARS:
            if (valueIn != null) {
                if ((int) Math.log10(valueIn) + 1 > integerParam && integerParam > 0) {
                    StringBuilder sb = new StringBuilder(valueIn.toString().substring(integerParam));
                    finalValue = Integer.parseInt(sb.toString());
                }
            }
            break;
        case REMOVE_LAST_CHARS:
            if (valueIn != null) {
                if ((int) Math.log10(valueIn) + 1 > integerParam && integerParam > 0) {
                    finalValue = valueIn / (int) Math.pow(10.0, integerParam);
                }
            }
            break;
        case REPLACE_FIRST_CHARS:
            if (valueIn != null) {
                if (integerParam > 0) {
                    if ((int) Math.log10(valueIn) + 1 <= integerParam) {
                        integerParam = (int) Math.log10(valueIn) + 1;
                    }
                    StringBuilder sbu = new StringBuilder(valueIn.toString());
                    StringBuilder remp = new StringBuilder(EMPTY_STRING);
                    for (int i = 0; i < integerParam; ++i) {
                        remp.append(rnd.nextInt(9));
                    }
                    sbu.replace(0, integerParam, remp.toString());
                    finalValue = Integer.parseInt(sbu.toString());
                }
            }
            break;
        case REPLACE_LAST_CHARS:
            if (valueIn != null) {
                if (integerParam > 0) {
                    if ((int) Math.log10(valueIn) + 1 < integerParam) {
                        integerParam = (int) Math.log10(valueIn) + 1;
                    }
                    StringBuilder sbui = new StringBuilder(valueIn.toString());
                    StringBuilder rempl = new StringBuilder(EMPTY_STRING);
                    for (int i = 0; i < integerParam; ++i) {
                        rempl.append(rnd.nextInt(9));
                    }
                    sbui.replace(sbui.length() - integerParam, sbui.length(), rempl.toString());
                    finalValue = Integer.parseInt(sbui.toString());
                }
            }
            break;
        case KEEP_FIRST_AND_GENERATE:
            if (valueIn != null) {
                if (integerParam > 0) {
                    if ((int) Math.log10(valueIn) + 1 < integerParam) {
                        integerParam = 0;
                    }
                    StringBuilder sb = new StringBuilder(valueIn.toString().substring(0, integerParam));
                    for (int i = integerParam; i < valueIn.toString().length(); ++i) {
                        sb.append(rnd.nextInt(9));
                    }
                    finalValue = Integer.parseInt(sb.toString());
                }
            }
            break;
        case KEEP_LAST_AND_GENERATE:
            if (valueIn != null) {
                if (integerParam > 0) {
                    if ((int) Math.log10(valueIn) + 1 < integerParam) {
                        integerParam = 0;
                    }
                    StringBuilder sb = new StringBuilder(EMPTY_STRING);
                    for (int i = 0; i < valueIn.toString().length() - integerParam; ++i) {
                        sb.append(rnd.nextInt(9));
                    }
                    sb.append(valueIn.toString().substring(valueIn.toString().length() - integerParam,
                            valueIn.toString().length()));
                    finalValue = Integer.parseInt(sb.toString());
                }
            }
            break;
        case GENERATE_SEQUENCE:
            finalValue = seq++;
            break;
        default:
            return finalValue;
        }
        return finalValue;
    }
}