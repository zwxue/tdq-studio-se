// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataquality.matching.date.pattern;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Hallam Mohamed Amine
 * @date 11/08/2009
 */
public class DatePatternRetriever {

    // TODO put these mappings in a file so that it can be easily enriched
    private static final String[][] MODEL2REGEX = {
            { "YYYY MM DD HH:MM:SS",
                    "^(19|20)[0-9]{2}(-|/| )([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9] ([0-1]?[0-9]|2[0-4]):([0-5][0-9]):[0-5][0-9]$" },
            { "YYYY DD MM HH:MM:SS",
                    "^(19|20)[0-9]{2}(-|/| )[0-3][0-9](-|/| )([0-0][1-9]|10|11|12) ([0-1]?[0-9]|2[0-4]):([0-5][0-9]):[0-5][0-9]$" },
            { "YYYY MM DD HH:MM",
                    "^(19|20)[0-9]{2}(-|/| )([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9] ([0-1]?[0-9]|2[0-4]):([0-5][0-9])$" },
            { "YYYY DD MM HH:MM",
                    "^(19|20)[0-9]{2}(-|/| )[0-3][0-9](-|/| )([0-0][1-9]|10|11|12) ([0-1]?[0-9]|2[0-4]):([0-5][0-9])$" },
            { "YYYY MM DD", "^(19|20)[0-9]{2}(-|/| )([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9]$" },
            { "YYYY DD MM", "^(19|20)[0-9]{2}(-|/| )[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)$" },
            { "DD MM YYYY", "^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2}$" },
            { "MM DD YYYY", "^([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9](-|/| )(19|20)[0-9]{2}$" },
            { "DD MM YYYY HH:MM:SS",
                    "^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9]):[0-5][0-9]$" },
            { "MM DD YYYY HH:MM:SS",
                    "^([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9](-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9]):[0-5][0-9]$" },
            { "DD MM YYYY HH:MM",
                    "^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9])$" },
            { "MM DD YYYY HH:MM",
                    "^([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9](-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9])$" },
            { "DD MM YYYY HH:MM tt",
                    "^[0-3][0-9](-|/| )([0-0][1-9]|10|11|12)(-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9]) (AM|PM)$" },
            { "MM DD YYYY HH:MM tt",
                    "^([0-0][1-9]|10|11|12)(-|/| )[0-3][0-9](-|/| )(19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9]) (AM|PM)$" },
            { "DD MMM YYYY", "^[0-3][0-9](-|/| )(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)(-|/| )(19|20)[0-9]{2}$" },
            {
                    "dddd, dd MMMM yyyy",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2}$" },
            {
                    "dddd, dd MMMM yyyy hh:mm",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-1][0-9]|2[0-4]):([0-5][0-9])$" },
            {
                    "dddd, dd MMMM yyyy hh:mm tt",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-1][0-9]|2[0-4]):([0-5][0-9]) (AM|PM)$" },

            {
                    "dddd, dd MMMM yyyy h:mm",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-9]|2[0-4]):([0-5][0-9])$" },

            {
                    "dddd, dd MMMM yyyy h:mm tt",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-9]|2[0-4]):([0-5][0-9]) (AM|PM)$" },

            {
                    "dddd, dd MMMM yyyy hh:mm:ss",
                    "^(Sunday|Monday|Tuesday|Wednesday|Thursday|Friday|Saturday), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-1][0-9]|2[0-4]):([0-5][0-9]):[0-5][0-9]$" },

            {
                    "ddd, dd MMMM yyyy hh:mm:ss GMT",
                    "^(Sun|Mon|Tue|Wed|Thu|Fri|Sat), [0-3][0-9] (January|February|March|April|May|June|July|August|September|October|November|December) (19|20)[0-9]{2} ([0-1]?[0-9]|2[0-4]):([0-5][0-9]):([0-5][0-9]) GMT$" },
            { "MMMM dd", "^(January|February|March|April|May|June|July|August|September|October|November|December) [0-3][0-9]$" },

            { "MMMM dd YYYY",
                    "(^January|February|March|April|May|June|July|August|September|October|November|December) [0-3][0-9] (19|20)[0-9]{2}$" },
            { "YYYY MMMM",
                    "^(19|20)[0-9]{2} (January|February|March|April|May|June|July|August|September|October|November|December)$" } };

    private List<ModelMatcher> modelMatchers = new ArrayList<ModelMatcher>();

    // constructor
    public DatePatternRetriever() {
        // TODO initialization should be done in a method
        // initialize all pattern names
        for (int i = 0; i < MODEL2REGEX.length; i++) {
            String[] model = MODEL2REGEX[i];
            modelMatchers.add(new ModelMatcher(model[0], model[1]));
        }
    }

    public void initModel2Regex(String[][] model2regex) {
        // TODO initialize model matchers here
    }

    public void handle(String expression) {
        // TODO check matching and update weights
    }

    // this method returns patterns
    public void toPattern(String expression) {
        // TODO this method should return an ordered list of objects which contain the model and the score

        for (ModelMatcher patternMatcher : this.modelMatchers) {
            if (patternMatcher.matches(expression)) {
                // affichage integr�
                System.out.println(patternMatcher.getModel());
                System.out.println("----------------------");

            } else {
                // not matched
            }
        }
        // showMyResults(myResults) ;
    }

    /*
     * methode qui affiche les r�sulatats public static void showMyResults(List<String> MyResults){ for (Iterator
     * iterator = MyResults.iterator(); iterator.hasNext();) { String string = (String) iterator.next(); //
     * System.out.println(MyResults.iterator().next()); } }
     */
}
