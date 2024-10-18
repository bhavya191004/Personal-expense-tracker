import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

class Expense {
    String category;
    String description;
    double amount;

    public Expense(String category, String description, double amount) {
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("%s - %s: $%.2f", category, description, amount);
    }
}

public class PersonalExpenseTrackerGUI extends JFrame {
    private static final String FILE_NAME = "expenses.txt";
    private ArrayList<Expense> expenses;
    private DefaultTableModel tableModel;
    private JTextField categoryField, descriptionField, amountField;

    public PersonalExpenseTrackerGUI() {
        // Initialize the list of expenses and load saved data
        expenses = new ArrayList<>();
        loadExpenses();

        // Setup the main window (JFrame)
        setTitle("Personal Expense Tracker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Setup the expense table
        String[] columns = {"Category", "Description", "Amount"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        add(scrollPane, BorderLayout.CENTER);

        // Setup the input panel for adding new expenses
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        inputPanel.add(categoryField);

        inputPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("Amount:"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        JButton addButton = new JButton("Add Expense");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });
        inputPanel.add(addButton);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedExpense(expenseTable.getSelectedRow());
            }
        });
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.SOUTH);

        // Populate table with data from expenses
        loadTableData();
        setVisible(true); // Show the window
    }

    // Add a new expense from the input fields
    private void addExpense() {
        String category = categoryField.getText();
        String description = descriptionField.getText();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount.");
            return;
        }

        Expense newExpense = new Expense(category, description, amount);
        expenses.add(newExpense);
        tableModel.addRow(new Object[]{category, description, amount});

        // Clear input fields after adding
        categoryField.setText("");
        descriptionField.setText("");
        amountField.setText("");

        saveExpenses(); // Save the new expense to file
    }

    // Delete the selected expense from the table and list
    private void deleteSelectedExpense(int selectedRow) {
        if (selectedRow >= 0) {
            expenses.remove(selectedRow);
            tableModel.removeRow(selectedRow);
            saveExpenses();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an expense to delete.");
        }
    }

    // Load expenses from the file and populate the table
    private void loadTableData() {
        for (Expense expense : expenses) {
            tableModel.addRow(new Object[]{expense.category, expense.description, expense.amount});
        }
    }

    // Save expenses to a file
    private void saveExpenses() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Expense expense : expenses) {
                writer.write(expense.category + "," + expense.description + "," + expense.amount);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving expenses: " + e.getMessage());
        }
    }

    // Load expenses from a file
    private void loadExpenses() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String category = parts[0];
                String description = parts[1];
                double amount = Double.parseDouble(parts[2]);
                expenses.add(new Expense(category, description, amount));
            }
        } catch (IOException e) {
            System.out.println("No existing expenses found. Starting fresh.");
        }
    }

    public static void main(String[] args) {
        // Create and display the expense tracker GUI
        new PersonalExpenseTrackerGUI();
    }
}
