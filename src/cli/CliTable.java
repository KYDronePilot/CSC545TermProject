package cli;

import java.util.ArrayList;
import java.util.List;

/**
 * For generating ASCII tables on the command line.
 */
class CliTable {

    // Column names for the table header
    String[] header;
    // Rows of column values
    public List<String[]> rows;

    /**
     * Construct table with headers.
     *
     * @param headers table headers
     */
    public CliTable(String[] headers) {
        this.header = headers;
        this.rows = new ArrayList<String[]>();
    }

    /**
     * Add a row to the table.
     *
     * @param row column values for row
     */
    public void append(String[] row) {
        rows.add(row);
    }

    /**
     * Find the maximum char width of a particular column.
     *
     * @param colI column index to check
     * @return max width of column
     */
    private int maxColWidth(int colI) {
        // Start with column name
        int maxWidth = header[colI].length();
        // Check each row
        for (String[] row : rows) {
            if (row[colI].length() > maxWidth) {
                maxWidth = row[colI].length();
            }
        }
        return maxWidth;
    }

    /**
     * Repeat and return the string `count` times
     *
     * @param s the string to repeat
     * @return repeated string
     */
    private String repeatString(String s, Integer count) {
        String res = "";
        for (int i = 0; i < count; i++) {
            res += s;
        }
        return res;
    }

    /**
     * Generate the ASCII table.
     *
     * @return ASCII table
     */
    @Override
    public String toString() {
        // Get max widths for each column
        int[] maxWidths = new int[header.length];
        for (int i = 0; i < header.length; i++) {
            maxWidths[i] = maxColWidth(i);
        }
        // Generate a horizontal divider
        String divider = "+";
        for (int maxWidth : maxWidths) {
            divider += repeatString("-", maxWidth + 2) + "+";
        }
        // Generate header
        String headerRow = "|";
        for (int i = 0; i < header.length; i++) {
            headerRow += " " + String.format("%-" + maxWidths[i] + "s", header[i]) + " |";
        }
        // Start to merge stuff
        String tableText = divider + "\n" + headerRow + "\n" + divider + "\n";
        // Generate and join data rows
        for (String[] row : rows) {
            tableText += "|";
            for (int i = 0; i < header.length; i++) {
                tableText += " " + String.format("%-" + maxWidths[i] + "s", row[i]) + " |";
            }
            tableText += "\n";
        }
        return tableText + divider;
    }
}
