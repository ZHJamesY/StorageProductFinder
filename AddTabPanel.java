import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.regex.Pattern;

public class AddTabPanel 
{
    public static JTable taskTable;
    public static DefaultTableModel model;

    public static JPanel addPanel() throws IOException
    {
        JPanel add = new JPanel();

        // Location
        String[] locationNameList = new String[]{"", "A", "B", "C", "D", "E", "F", "G"};
        JComboBox<String> locationList = new JComboBox<>(locationNameList);
        JLabel locationLabel = new JLabel("Location");
        JTextField locationText = new JTextField(5);
        JButton locationButton = new JButton("Add");
        JLabel locationMsg = new JLabel("");

        // Add amount
        JLabel amountLabel = new JLabel("Amount");
        JTextField amountText = new JTextField(15);
        JLabel amountMsg = new JLabel("");

        // Tasks table
        String[] taskHeadings = new String[]{"ID", "Name", "Amount"};
        model = new DefaultTableModel(taskHeadings, 0);
        taskTable = new JTable(model)
        {
            // Cell not editable
            public boolean isCellEditable(int data, int columns)
            {
                return false;
            }
        };
        // Columns not movable
        taskTable.getTableHeader().setReorderingAllowed(false);

        // Center all cells data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        taskTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        taskTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        taskTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );

        AddProduct.initializeTaskList(model);

        JScrollPane taskScroll = new JScrollPane(taskTable);

        // Delete task 
        JButton deleteTaskButton = new JButton("Delete task(s)");

        add.setLayout(null);

        // Location
        locationLabel.setBounds(30, 30, 100, 25);
        locationList.setBounds(90, 30, 40, 25);
        locationText.setBounds(140, 30, 50, 25);
        locationButton.setBounds(200, 30, 100, 25);
        locationMsg.setBounds(310, 30, 300, 25);

        // Amount
        amountLabel.setBounds(30, 70, 100, 25);
        amountText.setBounds(90, 70, 50, 25);
        amountMsg.setBounds(150, 70, 300, 25);

        // Task table
        taskScroll.setBounds(45, 180, 500, 260);

        // Delete task
        deleteTaskButton.setBounds(240, 460, 120, 25);

        // Location
        add.add(locationLabel);
        add.add(locationList);
        add.add(locationText);
        add.add(locationButton);
        add.add(locationMsg);

        // Amount
        add.add(amountLabel);
        add.add(amountText);
        add.add(amountMsg);
        
        // Task Table
        add.add(taskScroll);

        // Delete task
        add.add(deleteTaskButton);

        // Delete tasks from table by clicking deleteTaskButton
        deleteTaskButton.addActionListener((ActionListener) new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try 
                {
                    // Delete selected rows of task
                    int[] deleteIndex = taskTable.getSelectedRows();
                    AddProduct.deleteAddedTask(deleteIndex, "NO");
                    for(int i = 0; i < deleteIndex.length; i++)
                    {
                        model.removeRow(deleteIndex[i]);
                    }
                } 
                catch (IOException e1) 
                {
                    e1.printStackTrace();
                }
            }
        });

        // Write to Name.txt, ID.txt, and Location.txt file by clicking locationButton
        locationButton.addActionListener((ActionListener) new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(model.getRowCount() != 0)
                {
                    if(locationList.getSelectedItem().toString() != "")
                    {
                        if(!locationText.getText().isBlank() && AddProduct.isNumeric(locationText.getText()) && positionFormat(locationText.getText().strip()) && AddProduct.correctFormat(locationText.getText()))
                        {
                            if((!amountText.getText().contains(".") && AddProduct.isNumeric(amountText.getText()) && Integer.parseInt(amountText.getText()) < Integer.parseInt(model.getValueAt(0,2).toString()) && Integer.parseInt(amountText.getText()) > 0) || amountText.getText().isEmpty())
                            {
                                try 
                                {
                                    // Write to Name.txt and ID.txt
                                    int result = AddProduct.createAllProducts(taskTable.getValueAt(0, 0).toString(), taskTable.getValueAt(0, 1).toString(), locationStr(locationList.getSelectedItem().toString(), locationText.getText()), amountText.getText().strip());
                                    
                                    SearchProduct.addProductName(SearchTabPanel.model, taskTable.getValueAt(0, 1).toString());

                                    if(result == -1)
                                    {
                                        // Delete Tasks.txt's added task from table, store all amount at once
                                        AddProduct.deleteAddedTask(taskTable.getSelectedRows(), "YES");
                                        model.removeRow(0);
                                    }
                                    else
                                    {
                                        // Store to different location
                                        AddProduct.diffLocationAmount(model, amountText.getText());
                                    }
                                    amountText.setText(null);

                                } 
                                catch (IOException | InterruptedException e1) 
                                {
                                    e1.printStackTrace();
                                }
                                // success msg
                                locationMsg.setText("Product added successfully!");
                            }
                            // Error msg
                            else 
                            {
                            amountMsg.setText("Amount error / Enter positive integer amount"); 
                            amountText.setText("");
                            }
                        }
                        // Error msg
                        else locationMsg.setText("Enter position (123): Shelf level: 1, position: 23");
                    }
                    // Error msg
                    else locationMsg.setText("Select a location");
                }
                // Error msg
                else locationMsg.setText("Task list is empty");

                // msg timer set string to ""
                if(!locationMsg.getText().equals("") || !amountMsg.getText().equals(""))
                {
                    Timer timer = new Timer(3000, event -> 
                    {
                        locationMsg.setText("");
                        amountMsg.setText("");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
        return add;
    }

    // Return location + position string
    public static String locationStr(String location, String position)
    {
        return location + "-" + position;
    }

    // position text format
    public static Boolean positionFormat(String position)
    {
        return Pattern.matches("\\d..", position) && Pattern.matches(".\\d.", position) && Pattern.matches("..\\d", position); 
    }
}
