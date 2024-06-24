import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeInfoGUI {

    // HashMap to store employee data
    private static Map<String, String[]> employeeData = new HashMap<>();

    public static void main(String[] args) {
        String tsvFile = "employees.tsv"; // Path to your TSV file
        String line;
        String tsvSplitBy = "\t"; // Use tab as the delimiter for TSV

        // Load employee data from TSV file
        try (BufferedReader br = new BufferedReader(new FileReader(tsvFile))) {
            while ((line = br.readLine()) != null) {
                String[] employee = line.split(tsvSplitBy);
                employeeData.put(employee[0].trim(), employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the GUI
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Employee Info");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input Panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));

        JLabel labelEmployeeNumber = new JLabel("Employee Number:");
        JTextField textEmployeeNumber = new JTextField();
        JLabel labelHoursWorked = new JLabel("Hours Worked in a Week:");
        JTextField textHoursWorked = new JTextField();
        JButton buttonCalculate = new JButton("Calculate Salary");

        inputPanel.add(labelEmployeeNumber);
        inputPanel.add(textEmployeeNumber);
        inputPanel.add(labelHoursWorked);
        inputPanel.add(textHoursWorked);
        inputPanel.add(new JLabel()); // Placeholder
        inputPanel.add(buttonCalculate);

        // Result Area
        JTextArea textAreaResult = new JTextArea();
        textAreaResult.setEditable(false);
        textAreaResult.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textAreaResult);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Result"));

        // Add input panel and result area to main panel
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main panel to frame
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        // Action listener for calculate button
        buttonCalculate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String employeeNumber = textEmployeeNumber.getText().trim();
                    if (employeeNumber.isEmpty()) {
                        throw new IllegalArgumentException("Employee number cannot be empty.");
                    }

                    int hoursWorked = Integer.parseInt(textHoursWorked.getText().trim());
                    if (hoursWorked <= 0) {
                        throw new IllegalArgumentException("Hours worked must be greater than zero.");
                    }

                    String result = calculateAndDisplaySalary(employeeNumber, hoursWorked);
                    textAreaResult.setText(result);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Invalid input for hours worked. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static String calculateAndDisplaySalary(String employeeNumber, int hoursWorked) {
        if (employeeData.containsKey(employeeNumber)) {
            String[] employee = employeeData.get(employeeNumber);
            try {
                double hourlyRate = Double.parseDouble(employee[18].trim()); // Assuming hourly rate is at index 18
                double grossSalary = hoursWorked * hourlyRate;

                // Calculate SSS contribution
                double sssContributionRate = 0.045; // Employee's share of SSS contribution is 4.5%
                double sssContribution = grossSalary * sssContributionRate;

                // Calculate PhilHealth contribution
                double philhealthContributionRate = 0.025; // Employee's share of PhilHealth contribution is 2.5%
                double philhealthContribution = grossSalary * philhealthContributionRate;

                // Calculate Pag-IBIG contribution
                double pagibigContributionRate = 0.03; // Pag-IBIG contribution is 3%
                double pagibigContribution = grossSalary * pagibigContributionRate;

                // Calculate net salary after deductions
                double netSalary = grossSalary - sssContribution - philhealthContribution - pagibigContribution;

                return "Gross Salary: P" + String.format("%.2f", grossSalary) + "\n" +
                       "SSS Contribution (4.5%): P" + String.format("%.2f", sssContribution) + "\n" +
                       "PhilHealth Contribution (2.5%): P" + String.format("%.2f", philhealthContribution) + "\n" +
                       "Pag-IBIG Contribution (3%): P" + String.format("%.2f", pagibigContribution) + "\n" +
                       "Net Salary after SSS, PhilHealth, and Pag-IBIG Contributions: P" + String.format("%.2f", netSalary);

            } catch (NumberFormatException e) {
                return "Invalid hourly rate format for employee.";
            }
        } else {
            return "Employee not found.";
        }
    }
}
