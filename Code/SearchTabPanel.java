import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class SearchTabPanel 
{
    public static DefaultComboBoxModel<String> model;
    public static DefaultTableModel tableModel;

    public static JPanel findPanel() throws IOException
    {
        JPanel search = new JPanel();

        // search: label, text field, button, msg
        JLabel searchManual = new JLabel("Search the earliest inbound shipment for the selected product: ");
        JLabel searchName = new JLabel("Product Name");
        String[] nameListStr = SearchProduct.getAllProductNames();
        JComboBox<String> searchNameList = new JComboBox<>();
        JButton searchButton = new JButton("Search");
        JLabel searchMsg = new JLabel("");

        // center findNameList data
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(DefaultListCellRenderer.CENTER); // center-aligned items
        searchNameList.setRenderer(listRenderer);

        // Initialize product data
        model = new DefaultComboBoxModel<String>(nameListStr);
        searchNameList.setModel(model);

        // Search table
        String[] taskHeadings = new String[]{"ID", "Name", "Amount", "Location"};
        tableModel = new DefaultTableModel(taskHeadings, 0);
        JTable searchTable = new JTable(tableModel)
        {
            // Cell not editable
            public boolean isCellEditable(int data, int columns)
            {
                return false;
            }
        };
        // Columns not movable
        searchTable.getTableHeader().setReorderingAllowed(false);

        // Center all cells data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
        searchTable.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        searchTable.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        searchTable.getColumnModel().getColumn(2).setCellRenderer( centerRenderer );
        searchTable.getColumnModel().getColumn(3).setCellRenderer( centerRenderer );

        JScrollPane searchScroll = new JScrollPane(searchTable);

        // pick up
        JLabel pickupAmount = new JLabel("Amount");
        JTextField pickupText = new JTextField(15);
        JButton pickupButton = new JButton("Pick up");
        JLabel pickupMsg = new JLabel("");

        search.setLayout(null);

        // search:
        searchManual.setBounds(30, 20, 600, 25);
        searchName.setBounds(30, 55, 100, 25);;
        searchNameList.setBounds(115, 55, 200, 25);
        searchButton.setBounds(330, 55, 100, 25);
        searchMsg.setBounds(440, 55, 200, 25);

        // Search table 
        searchScroll.setBounds(45, 180, 500, 260);

        // Pick up product
        pickupAmount.setBounds(30, 100, 100, 25);
        pickupText.setBounds(115, 100, 100, 25);
        pickupButton.setBounds(230, 100, 100, 25);
        pickupMsg.setBounds(340, 100, 200, 25);


        // Search
        search.add(searchManual);
        search.add(searchName);
        search.add(searchNameList);
        search.add(searchButton);
        search.add(searchMsg);

        //pick up
        search.add(pickupAmount);
        search.add(pickupText);
        search.add(pickupButton);
        search.add(pickupMsg);

        //Search table
        search.add(searchScroll);

        // Display the earliest inbound information of the selected product by clicking searchButton
        searchButton.addActionListener((ActionListener) new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                tableModel.setRowCount(0);
                if(searchNameList.getSelectedItem() != " " && searchNameList.getSelectedItem() != "Product data is empty")
                {
                    // Search and add product to searchTable
                    Boolean ifFound = SearchProduct.searchProduct(tableModel, searchNameList.getSelectedItem().toString());
                    if(ifFound != true)
                    {
                        // Error msg
                        searchMsg.setText("Product does not exist");
                    }
                }
                // Error msg
                else searchMsg.setText("Product does not exist");

                // msg timer set string to ""
                Timer timer = new Timer(3000, event ->
                {
                    searchMsg.setText("");
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Pick up(delete) amount of product by clicking pickupButton
        pickupButton.addActionListener((ActionListener) new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(searchNameList.getSelectedItem() !=  " ")
                {
                    if(!pickupText.getText().isBlank() && SearchProduct.isNumeric(pickupText.getText().toString().strip()) && Integer.parseInt(pickupText.getText().toString().strip()) > 0)
                    {
                        int[] selectedRows = searchTable.getSelectedRows();
                        if(selectedRows.length > 0)
                        {                      
                            if(SearchProduct.isInRange(pickupText.getText().toString().strip(), tableModel ,selectedRows))
                            {
                                for(int i = selectedRows.length - 1; i >= 0; i--)
                                {
                                    try 
                                    {
                                        String selectedProduct = searchNameList.getSelectedItem().toString();
                                        int resultAmount = SearchProduct.pickUpProduct(searchNameList.getSelectedItem().toString(), pickupText.getText(), selectedRows[i], model, searchNameList, searchMsg);
                                    
                                        if(resultAmount == 0) // If no more such product
                                        {
                                            tableModel.removeRow(selectedRows[i]);

                                            // Product out of storage msg
                                            if(model.getIndexOf(selectedProduct) == -1)
                                            {
                                                searchMsg.setText("All " + selectedProduct + " is out of storage");
                                            }
                                        }
                                        else //if product still exist
                                        {
                                            tableModel.setValueAt(String.valueOf(resultAmount), selectedRows[i], 2);
                                        }
                                    } 
                                    catch (IOException e1) 
                                    {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                            // Error msg
                            else pickupMsg.setText("Amount exceeds");
                        }
                        // Error msg
                        else 
                        {
                            pickupMsg.setText("Select a product");
                        }
                    }
                    // Error msg
                    else pickupMsg.setText("Amount error / Enter positive integer amount");
                }
                // Error msg
                else searchMsg.setText("Select a product");
                pickupText.setText("");

                // msg timer set string to ""
                if(!pickupMsg.getText().equals("") || !searchMsg.getText().equals(""))
                {
                    Timer timer = new Timer(4000, event ->
                    {
                        pickupMsg.setText("");
                        searchMsg.setText("");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
        
        return search;
    }

    
}
