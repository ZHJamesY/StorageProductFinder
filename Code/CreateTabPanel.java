import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.*;

public class CreateTabPanel 
{
    public static JButton button;

    public static JPanel createPanel() throws IOException
    {
        JPanel create = new JPanel();

        // Create product
        JLabel label = new JLabel("Product Name");
        JTextField text = new JTextField(15);
        button = new JButton("Create");
        JLabel buttonMsg = new JLabel("");

        // Create product quantity
        JLabel productAmount = new JLabel("Amount");
        JTextField productAmountText = new JTextField(15);
        JLabel productAmountMsg = new JLabel("");
        

        // Inbound date
        JLabel inboundDate = new JLabel("Date");
        JTextField inboundDateText = new JTextField(15);
        JButton inboundDateButton = new JButton("Today");
        JLabel inboundDateMsg = new JLabel("Format: DD/MM/YY");
        
        // section layout
        create.setLayout(null);

        // Product name:
        label.setBounds(30, 30, 100, 25);
        text.setBounds(120, 30, 100, 25);
        button.setBounds(230, 30, 100, 25);
        buttonMsg.setBounds(350, 30, 500, 25);

        // Product Amount:
        productAmount.setBounds(30, 70, 100, 25);
        productAmountText.setBounds(120, 70, 100, 25);
        productAmountMsg.setBounds(230, 70, 500, 25);

        // Inbound date
        inboundDate.setBounds(30, 110, 100, 25);
        inboundDateText.setBounds(120, 110, 100, 25);
        inboundDateButton.setBounds(230, 110, 100, 25);
        inboundDateMsg.setBounds(340, 110, 500, 25);

        // Create product
        create.add(label);
        create.add(text);
        create.add(button);
        create.add(buttonMsg);

        // Inbound date
        create.add(inboundDate);
        create.add(inboundDateText);
        create.add(inboundDateButton);
        create.add(inboundDateMsg);

        // Product Amount:
        create.add(productAmount);
        create.add(productAmountText);
        create.add(productAmountMsg);
       
        // Add today's inbound date by clicking inboundDateButton
        inboundDateButton.addActionListener((ActionListener) new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                Date date = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
                String dateStr = formatter.format(date);
                inboundDateText.setText(dateStr);
            }
        });

        // Create inbound product by clicking button
        button.addActionListener((ActionListener) new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                if(!text.getText().isEmpty() && !text.getText().isBlank())
                {
                    if(isNumeric(productAmountText.getText()) && !productAmountText.getText().contains(".") && Integer.parseInt(productAmountText.getText().toString().strip()) > 0)
                    {
                        if(isDate(inboundDateText.getText()))
                        {
                            try 
                            {
                                // Write to Tasks txt file
                                CreateProduct.createAddToTasks(inboundDateText.getText(), (text.getText()).toLowerCase().strip(), productAmountText.getText().strip());
                                text.setText(null);
                                productAmountText.setText(null);
                                inboundDateText.setText(null);

                                // Add task to add product tab
                                AddProduct.addTaskList(AddTabPanel.model);
                                
                                // success msg
                                buttonMsg.setText("Product created successfully !");
                            } 
                            catch (IOException e1) 
                            {
                                e1.printStackTrace();
                            } 
                        }
                        else
                        {
                            // Error msg
                            inboundDateText.setText(null);
                            inboundDateText.requestFocusInWindow();
                            inboundDateMsg.setText("Enter date format (DD/MM/YY)");
                        } 
                    }
                    else
                    {
                        // Error msg
                        productAmountText.setText(null);
                        productAmountText.requestFocusInWindow();
                        productAmountMsg.setText("Enter a positive integer number");
                    } 
                }
                else
                {
                    // Error msg
                    text.setText(null);
                    text.requestFocusInWindow();
                    buttonMsg.setText("Enter a product name");
                }
                
                // msg timer set string to ""
                if(!productAmountMsg.getText().equals("") || !inboundDateMsg.getText().equals("Format: DD/MM/YY") || !buttonMsg.getText().equals(""))
                {
                    Timer timer = new Timer(3000, event -> 
                    {
                        productAmountMsg.setText("");
                        inboundDateMsg.setText("Format: DD/MM/YY");
                        buttonMsg.setText("");
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        });
        return create;
    }

    // Check if string is numeric
    public static boolean isNumeric(String str) 
    { 
        try 
        {  
            Double.parseDouble(str);  
            return true;
        } 
        catch(NumberFormatException e)
        {  
            return false;  
        }  
    }

    // Check string is date format
    public static boolean isDate(String str)
    {
        return Pattern.matches("../../..", str) && Pattern.matches("\\d\\d.\\d\\d.\\d\\d", str); 
    }
}
