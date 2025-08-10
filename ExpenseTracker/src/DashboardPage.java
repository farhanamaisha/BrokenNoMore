import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.text.SimpleDateFormat;

import com.toedter.calendar.JDateChooser;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

// iText 2.1.7 imports
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class DashboardPage extends JFrame {
    private JLabel incomeLabel, expenseLabel, balanceLabel, goalLabel, reminderLabel;
    private JTextField goalInput;
    private double totalIncome = 0, totalExpense = 0, monthlyGoal = 10000;
    private final Map<Date, List<Entry>> entryMap = new TreeMap<>();
    private final DefaultPieDataset pieDataset = new DefaultPieDataset();
    private ChartPanel chartPanel;
    private JTextArea dailyView;
    private JTextField searchField;
    private JDateChooser dateChooser;
    private String username, password;

    // For consistent date formatting in UI and PDF
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public DashboardPage(String username, String password) {
        this.username = username;
        this.password = password;

        setTitle("BrokenNoMore Dashboard - User: " + username);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(220, 250, 240));

        JLabel header = new JLabel("BrokenNoMore Dashboard");
        header.setFont(new Font("Segoe UI", Font.BOLD, 28));
        header.setBounds(30, 10, 400, 40);
        add(header);

        incomeLabel = createCard("Income", 30, 60, new Color(200, 230, 255));
        expenseLabel = createCard("Expenses", 260, 60, new Color(255, 210, 210));
        balanceLabel = createCard("Balance", 490, 60, new Color(255, 245, 180));
        add(incomeLabel);
        add(expenseLabel);
        add(balanceLabel);

        JLabel goalTextLabel = new JLabel("Set Monthly Saving Goal (৳):");
        goalTextLabel.setBounds(30, 130, 200, 25);
        add(goalTextLabel);

        goalInput = new JTextField(Double.toString(monthlyGoal));
        goalInput.setBounds(230, 130, 100, 25);
        add(goalInput);

        JButton setGoalBtn = new JButton("Set Goal");
        setGoalBtn.setBounds(340, 130, 100, 25);
        add(setGoalBtn);

        setGoalBtn.addActionListener(e -> {
            try {
                double newGoal = Double.parseDouble(goalInput.getText());
                if (newGoal >= 0) {
                    monthlyGoal = newGoal;
                    updateDashboard();
                    saveEntries();
                } else {
                    JOptionPane.showMessageDialog(this, "Goal must be non-negative.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid number for goal.");
            }
        });

        goalLabel = new JLabel("Goal remaining: ৳" + String.format("%.2f", monthlyGoal));
        goalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        goalLabel.setBounds(30, 160, 400, 25);
        add(goalLabel);

        reminderLabel = new JLabel("");
        reminderLabel.setForeground(Color.RED);
        reminderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        reminderLabel.setBounds(30, 190, 700, 25);
        add(reminderLabel);

        searchField = new JTextField();
        searchField.setBounds(30, 225, 230, 28);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setToolTipText("Filter by category...");
        searchField.addActionListener(e -> showDayEntries(getSelectedDate()));
        add(searchField);

        JButton searchBtn = new JButton("Filter");
        searchBtn.setBounds(265, 225, 90, 28);
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        add(searchBtn);
        searchBtn.addActionListener(e -> showDayEntries(getSelectedDate()));

        dateChooser = new JDateChooser();
        dateChooser.setBounds(30, 270, 200, 30);
        add(dateChooser);

        JButton viewBtn = new JButton("Show Entries");
        viewBtn.setBounds(240, 270, 120, 30);
        add(viewBtn);

        dailyView = new JTextArea();
        dailyView.setEditable(false);
        dailyView.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(dailyView);
        scroll.setBounds(30, 310, 330, 310);
        add(scroll);

        viewBtn.addActionListener(e -> {
            Date selectedDate = getSelectedDate();
            if (selectedDate != null) {
                showDayEntries(selectedDate);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date.");
            }
        });

        pieDataset.setValue("No data", 1);
        JFreeChart chart = ChartFactory.createPieChart("Spending by Category", pieDataset, true, true, false);
        chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(380, 310, 580, 300);
        add(chartPanel);

        JButton btnIncome = new JButton("Add Income");
        btnIncome.setBounds(750, 80, 180, 35);
        btnIncome.addActionListener(e -> showAddDialog(true));
        add(btnIncome);

        JButton btnExpense = new JButton("Add Expense");
        btnExpense.setBounds(750, 130, 180, 35);
        btnExpense.addActionListener(e -> showAddDialog(false));
        add(btnExpense);

        JButton btnExportPDF = new JButton("Export to PDF");
        btnExportPDF.setBounds(750, 180, 180, 35);
        btnExportPDF.addActionListener(e -> exportToPDF());
        add(btnExportPDF);

        JButton btnAIHelper = new JButton("AI Helper");
        btnAIHelper.setBounds(850, 620, 120, 30);
        btnAIHelper.addActionListener(e -> showAIHelper());
        add(btnAIHelper);

        loadEntries();

        goalInput.setText(String.format("%.2f", monthlyGoal));

        updateDashboard();
        setVisible(true);
    }

    private void saveEntries() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(username + "_entries.dat"))) {
            oos.writeObject(entryMap);
            oos.writeDouble(totalIncome);
            oos.writeDouble(totalExpense);
            oos.writeDouble(monthlyGoal);
        } catch (IOException e) {
            System.err.println("Error saving entries: " + e.getMessage());
        }
    }

    private void loadEntries() {
        File f = new File(username + "_entries.dat");
        if (!f.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Map<Date, List<Entry>> loadedMap = (Map<Date, List<Entry>>) ois.readObject();
            entryMap.clear();
            // IMPORTANT: truncate keys on load to normalize them
            for (Map.Entry<Date, List<Entry>> e : loadedMap.entrySet()) {
                entryMap.put(truncateTime(e.getKey()), e.getValue());
            }
            totalIncome = ois.readDouble();
            totalExpense = ois.readDouble();
            monthlyGoal = ois.readDouble();
        } catch (Exception e) {
            System.err.println("Error loading entries: " + e.getMessage());
        }
    }

    private Date getSelectedDate() {
        Date date = dateChooser.getDate();
        return truncateTime(date);
    }

    private JLabel createCard(String title, int x, int y, Color bg) {
        JLabel lbl = new JLabel(title + ": ৳0", SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setBounds(x, y, 200, 60);
        return lbl;
    }

    private void showAddDialog(boolean isIncome) {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField amt = new JTextField();
        JDateChooser dc = new JDateChooser();
        JComboBox<String> cb = new JComboBox<>(new String[]{"Food", "Transport", "Rent", "Salary", "Other"});

        panel.add(new JLabel("Amount:"));
        panel.add(amt);
        panel.add(new JLabel("Date:"));
        panel.add(dc);
        panel.add(new JLabel("Category:"));
        panel.add(cb);

        int res = JOptionPane.showConfirmDialog(this, panel, isIncome ? "Add Income" : "Add Expense", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amt.getText());
                Date selectedDate = dc.getDate();
                if (selectedDate == null) {
                    JOptionPane.showMessageDialog(this, "Please select a valid date.");
                    return;
                }
                selectedDate = truncateTime(selectedDate); // Truncate time here
                Entry entry = new Entry(isIncome ? "Income" : "Expense", amount, (String) cb.getSelectedItem());
                entryMap.computeIfAbsent(selectedDate, k -> new ArrayList<>()).add(entry);
                if (isIncome) totalIncome += amount;
                else totalExpense += amount;
                updateDashboard();
                saveEntries();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount.");
            }
        }
    }

    private void updateDashboard() {
        incomeLabel.setText("Income: ৳" + String.format("%.2f", totalIncome));
        expenseLabel.setText("Expenses: ৳" + String.format("%.2f", totalExpense));
        double balance = totalIncome - totalExpense;
        balanceLabel.setText("Balance: ৳" + String.format("%.2f", balance));

        double remaining = monthlyGoal - balance;
        if (remaining > 0) {
            goalLabel.setText("Goal remaining: ৳" + String.format("%.2f", remaining));
        } else {
            goalLabel.setText("Goal reached!");
        }

        if (balance < 500 && balance >= 0) {
            reminderLabel.setText("Warning: Your balance is low (below ৳500)!");
        } else if (balance < 0) {
            reminderLabel.setText("Alert: Your balance is negative!");
        } else if (remaining < 0) {
            reminderLabel.setText("Congrats! You’ve exceeded your monthly goal!");
        } else if (remaining < 1000) {
            reminderLabel.setText("Warning: You’re very close to your monthly goal.");
        } else {
            reminderLabel.setText("");
        }

        pieDataset.clear();
        Map<String, Double> categorySums = new HashMap<>();
        for (List<Entry> list : entryMap.values()) {
            for (Entry e : list) {
                if (e.type.equals("Expense")) {
                    categorySums.put(e.category, categorySums.getOrDefault(e.category, 0.0) + e.amount);
                }
            }
        }
        if (categorySums.isEmpty()) pieDataset.setValue("No expenses", 1);
        else categorySums.forEach(pieDataset::setValue);
    }

    private void showDayEntries(Date date) {
        if (date == null) {
            dailyView.setText("Please select a date.");
            return;
        }
        Date truncatedDate = truncateTime(date);
        String filter = searchField.getText().trim().toLowerCase();
        List<Entry> entries = entryMap.getOrDefault(truncatedDate, Collections.emptyList());
        StringBuilder sb = new StringBuilder("Entries for " + dateFormat.format(truncatedDate) + ":\n\n");
        boolean found = false;
        for (Entry e : entries) {
            if (filter.isEmpty() || e.category.toLowerCase().contains(filter)) {
                sb.append(e.type).append(" - ").append(e.category).append(": ৳").append(String.format("%.2f", e.amount)).append("\n");
                found = true;
            }
        }
        if (!found) sb.append("No matching entries.");
        dailyView.setText(sb.toString());
    }

    private void showAIHelper() {
        JDialog dialog = new JDialog(this, "AI Helper Chatbot", false);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JTextField inputField = new JTextField();
        JButton sendBtn = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendBtn, BorderLayout.EAST);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(inputPanel, BorderLayout.SOUTH);

        sendBtn.addActionListener(e -> {
            String question = inputField.getText().trim().toLowerCase();
            if (question.isEmpty()) return;
            chatArea.append("You: " + question + "\n");
            inputField.setText("");
            String answer = generateAIResponse(question);
            chatArea.append("AI Helper: " + answer + "\n");
        });

        dialog.setVisible(true);
    }

    private String generateAIResponse(String question) {
        if (question.contains("balance")) {
            double balance = totalIncome - totalExpense;
            return "Your current balance is ৳" + String.format("%.2f", balance);
        } else if (question.contains("goal")) {
            double remaining = monthlyGoal - (totalIncome - totalExpense);
            if (remaining > 0) return "You need ৳" + String.format("%.2f", remaining) + " more to reach your monthly goal.";
            else return "Congrats! You've reached your monthly goal!";
        } else if (question.contains("income")) {
            return "Your total income so far is ৳" + String.format("%.2f", totalIncome);
        } else if (question.contains("expense") || question.contains("spending")) {
            return "Your total expenses so far are ৳" + String.format("%.2f", totalExpense);
        }
        return "Sorry, I can only answer questions about your balance, goal, income, and expenses.";
    }

    private void exportToPDF() {
        Document document = new Document();
        try {
            String fileName = username + "_entries.pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            document.add(new Paragraph("BrokenNoMore Expense Tracker - Entries for User: " + username));
            document.add(new Paragraph(" "));

            for (Map.Entry<Date, List<Entry>> dateEntry : entryMap.entrySet()) {
                document.add(new Paragraph("Date: " + dateFormat.format(dateEntry.getKey())));
                for (Entry e : dateEntry.getValue()) {
                    String line = e.type + " - " + e.category + ": ৳" + String.format("%.2f", e.amount);
                    document.add(new Paragraph(line));
                }
                document.add(new Paragraph(" "));
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Exported entries to PDF:\n" + fileName);
        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting to PDF: " + e.getMessage());
        }
    }

    // Helper to truncate time part of Date for consistent keys
    private Date truncateTime(Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardPage("Guest", null));
    }

    static class Entry implements Serializable {
        String type, category;
        double amount;
        Entry(String t, double a, String c) {
            type = t;
            amount = a;
            category = c;
        }
    }
}
