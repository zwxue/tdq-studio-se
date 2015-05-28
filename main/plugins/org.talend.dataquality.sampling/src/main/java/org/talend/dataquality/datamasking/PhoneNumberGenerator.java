package org.talend.dataquality.datamasking;

import org.talend.dataquality.duplicating.RandomWrapper;

public class PhoneNumberGenerator {

    @SuppressWarnings("unused")
    private static com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil
            .getInstance();

    private static RandomWrapper rnd = null;

    public PhoneNumberGenerator(RandomWrapper rnd) {
        PhoneNumberGenerator.rnd = rnd;
    }

    /**
     * This function generates a valid french phone number written in international format.
     * 
     * @return A string holding the phone number.
     */
    public String generatePhoneNumber() {
        StringBuilder result = new StringBuilder("+33 "); //$NON-NLS-1$
        result.append(rnd.nextInt(5) + 1);
        for (int i = 0; i < 9; ++i) {
            result.append(rnd.nextInt(9));
        }
        return result.toString();
    }
}
