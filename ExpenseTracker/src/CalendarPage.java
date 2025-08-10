import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CalendarPage extends JFrame {
    public CalendarPage(Map<String, int[]> monthlyData) {
        setTitle("Monthly Summary");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columns = {"Month", "Income (৳)", "Expenses (৳)", "Balance (৳)"};
        String[][] data = new String[monthlyData.size()][4];

        int row = 0;
        for (Map.Entry<String, int[]> entry : monthlyData.entrySet()) {
            String month = entry.getKey();
            int income = entry.getValue()[0];
            int expense = entry.getValue()[1];
            int balance = income - expense;

            data[row][0] = month;
            data[row][1] = String.valueOf(income);
            data[row][2] = String.valueOf(expense);
            data[row][3] = String.valueOf(balance);
            row++;
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(24);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        setVisible(true);
    }
}
