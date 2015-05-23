package org.talend.datascience.common.inference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class AnalyzerTest {
    protected static List<String[]> getRecords(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        try {
            List<String[]> records = new ArrayList<String[]>();
            final List<String> lines = IOUtils.readLines(inputStream);
            for (String line : lines) {
                String[] record = StringUtils.splitByWholeSeparatorPreserveAllTokens(line, ";");
                records.add(record);
            }
            return records;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                // Silent ignore
                e.printStackTrace();
            }
        }
    }
}
