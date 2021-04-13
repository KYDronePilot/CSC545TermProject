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
        var maxWidth = header[colI].length();
        // Check each row
        for (var row : rows) {
            if (row[colI].length() > maxWidth) {
                maxWidth = row[colI].length();
            }
        }
        return maxWidth;
    }

    /**
     * Generate the ASCII table.
     *
     * @return ASCII table
     */
    @Override
    public String toString() {
        // Get max widths for each column
        var maxWidths = new int[header.length];
        for (var i = 0; i < header.length; i++) {
            maxWidths[i] = maxColWidth(i);
        }
        // Generate a horizontal divider
        var divider = "+";
        for (var maxWidth : maxWidths) {
            divider += "-".repeat(maxWidth + 2) + "+";
        }
        // Generate header
        var headerRow = "|";
        for (var i = 0; i < header.length; i++) {
            headerRow += " " + String.format("%-" + maxWidths[i] + "s", header[i]) + " |";
        }
        // Start to merge stuff
        var tableText = divider + "\n" + headerRow + "\n" + divider + "\n";
        // Generate and join data rows
        for (var row : rows) {
            tableText += "|";
            for (var i = 0; i < header.length; i++) {
                tableText += " " + String.format("%-" + maxWidths[i] + "s", row[i]) + " |";
            }
            tableText += "\n";
        }
        return tableText + divider;
    }
}
