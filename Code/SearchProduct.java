import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchProduct 
{
    private static String allProductNamePath = "ProductData/AllProducts/Name.txt";
    private static String allProductIDPath = "ProductData/AllProducts/ID.txt";
    private static String allProductLocationPath = "ProductData/AllProducts/Location.txt";
    private static String allProductAmountPath = "ProductData/AllProducts/Amount.txt";

    // Get all product's name
    public static String[] getAllProductNames() throws IOException
    {
        AddProduct.makeDir();
        AddProduct.makeFile(allProductNamePath);

        List<String> nameList = new ArrayList<String>();
        nameList = Files.readAllLines(Paths.get(allProductNamePath));

        if(nameList.isEmpty())
        {
            return new String[]{" ", "Product data is empty"};
        }

        int size = nameList.size() + 1;
        String[] allNames = new String[size];
        allNames[0] = " ";
        for(int i = 1; i < size; i++)
        {
            allNames[i] = nameList.get(i - 1);
        }
        return allNames;
    }

    // Add product name to SearchTabPanel.searchNameList
    public static void addProductName(DefaultComboBoxModel<String> model, String name) throws IOException
    {
        List<String> nameList = new ArrayList<String>();
        nameList = Files.readAllLines(Paths.get(allProductNamePath));

        int index = nameList.indexOf(name);

        if(nameList.size() == 1)
        {
            model.removeElement("Product data is empty");
        }

        if(nameList.size() != model.getSize() - 1)
        {
            if(index != nameList.size() - 1)
            {
                model.insertElementAt(name, index + 1);
            }
            else model.addElement(name);
        }
    }

    // Search for desired product and add to searchTable using function: searchAddProduct(), return true if selected product exist else false
    public static Boolean searchProduct(DefaultTableModel tableModel, String name)
    {
        List<String> nameList = new ArrayList<String>();
        List<String> IDList = new ArrayList<String>();
        List<String> locationList = new ArrayList<String>();
        List<String> amountList = new ArrayList<String>();
        
        try 
        {
            nameList = Files.readAllLines(Paths.get(allProductNamePath));
            IDList = Files.readAllLines(Paths.get(allProductIDPath));
            locationList = Files.readAllLines(Paths.get(allProductLocationPath));
            amountList = Files.readAllLines(Paths.get(allProductAmountPath));

            if(nameList.contains(name))
            {
                int index = CreateProduct.binarySearch(nameList, name, 0, nameList.size(), 0);
                index = ((index * (-1)) -1);

                // Add to searchTable when there is only one ID
                if(IDList.get(index).indexOf(",") == IDList.get(index).lastIndexOf(","))
                {
                    searchAddProduct(tableModel, IDList.get(index).replace(",", ""), name, amountList.get(index).replace(",", ""), locationList.get(index).replace(",", ""));
                }
                else // Add to searchTable when there are more than one ID
                {
                    ArrayList<String> IDArray_List = new ArrayList<>(strToArrayList(IDList.get(index)));
                    ArrayList<String> locationArray_List = new ArrayList<>(strToArrayList(locationList.get(index)));
                    ArrayList<String> amountArray_List = new ArrayList<>(strToArrayList(amountList.get(index)));

                    searchAddProduct(tableModel, IDArray_List.get(0), name, amountArray_List.get(0), locationArray_List.get(0));
                    for(int i = 1; i < IDArray_List.size(); i++)
                    {
                        if(IDArray_List.get(i).substring(0, IDArray_List.get(i).indexOf("-")).toString().equals(IDArray_List.get(0).substring(0, IDArray_List.get(0).indexOf("-")).toString()))
                        {
                            searchAddProduct(tableModel, IDArray_List.get(i), name, amountArray_List.get(i), locationArray_List.get(i));
                        }
                    }
                }
                return true;
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        }

        return false;
    }

    // Add if product qualified to add on searchTable
    public static void searchAddProduct(DefaultTableModel tableModel, String ID, String name, String amount, String location)
    {
        tableModel.addRow(new Object[]{ID, name, amount, location});
    }

    // Put each word of input string into ArrayList and return
    public static ArrayList<String> strToArrayList(String string)
    {
        ArrayList<String> result = new ArrayList<String>();
        String word = "";
        for(int i = 0; i < string.length(); i++)
        {
            if(string.charAt(i) != ',')
            {
                word += string.charAt(i);
            }
            else if(string.charAt(i) == ',')
            {
                result.add(word);
                word = "";
            } 
        }

        return result;
    }

    // Take in a integer of selected searchTable rows, find the index match product, pick up(delete) an amount of this product or pick up(delete) all product, return the result amount
    public static int pickUpProduct(String selectedProduct, String selectedAmount, int selectedRow, DefaultComboBoxModel<String> model, JComboBox<String> searchNameList, JLabel searchMsg) throws IOException
    {
        List<String> nameList = new ArrayList<String>();
        List<String> IDList = new ArrayList<String>();
        List<String> locationList = new ArrayList<String>();
        List<String> amountList = new ArrayList<String>();

        nameList = Files.readAllLines(Paths.get(allProductNamePath));
        IDList = Files.readAllLines(Paths.get(allProductIDPath));
        locationList = Files.readAllLines(Paths.get(allProductLocationPath));
        amountList = Files.readAllLines(Paths.get(allProductAmountPath));

        int index = nameList.indexOf(selectedProduct);

        // if there is only one ID for the product
        if(IDList.get(index).indexOf(",") == IDList.get(index).lastIndexOf(",") && selectedAmount.equals(amountList.get(index).substring(0, amountList.get(index).length() - 1)))
        {
            nameList.remove(index);
            IDList.remove(index);
            locationList.remove(index);
            amountList.remove(index);

            PrintWriter nameWriter = new PrintWriter(allProductNamePath);
            PrintWriter IDWriter = new PrintWriter(allProductIDPath);
            PrintWriter locationWriter = new PrintWriter(allProductLocationPath);
            PrintWriter amountWriter = new PrintWriter(allProductAmountPath);

            for(String name: nameList)
            {
                nameWriter.println(name);
            }

            for(String id: IDList)
            {
                IDWriter.println(id);
            }

            for(String position: locationList)
            {
                locationWriter.println(position);
            }

            for(String eachAmount: amountList)
            {
                amountWriter.println(eachAmount);
            }

            nameWriter.close();
            IDWriter.close();
            locationWriter.close();
            amountWriter.close();

            model.removeElement(searchNameList.getSelectedItem().toString());
            model.setSelectedItem(null);
            if(searchNameList.getItemCount() == 1)
            {
                model.addElement("Product data is empty");
            }
            return 0;
        }
        else // When there are more than one ID for the product
        {
            ArrayList<String> amountArrayList = new ArrayList<>(SearchProduct.strToArrayList(amountList.get(index)));
            ArrayList<String> IDArrayList = new ArrayList<>(SearchProduct.strToArrayList(IDList.get(index)));
            ArrayList<String> locationArrayList = new ArrayList<>(SearchProduct.strToArrayList(locationList.get(index)));

            int resultAmount = Integer.parseInt(amountArrayList.get(selectedRow)) - Integer.parseInt(selectedAmount);

            amountArrayList.set(selectedRow, String.valueOf(resultAmount));
            
            // Remove ID, location and amount, when resultAmount == 0
            if(resultAmount == 0)
            {
                amountArrayList.remove(selectedRow);
                locationArrayList.remove(selectedRow);
                IDArrayList.remove(selectedRow);

                amountList.set(index, amountArrayList.toString().substring(1, amountArrayList.toString().length() - 1).replace(", ", ",") + ",");
                locationList.set(index, locationArrayList.toString().substring(1, locationArrayList.toString().length() - 1).replace(", ", ",") + ",");
                IDList.set(index, IDArrayList.toString().substring(1, IDArrayList.toString().length() - 1).replace(", ", ",") + ",");

            }
            else if(resultAmount != 0) // Assign new value when resultAmount != 0
            {
                amountList.set(index, amountArrayList.toString().substring(1, amountArrayList.toString().length() - 1).replace(", ", ",") + ",");
            }

            if(resultAmount == 0)
            {
                PrintWriter IDWriter = new PrintWriter(allProductIDPath);
                PrintWriter locationWriter = new PrintWriter(allProductLocationPath);

                for(String id: IDList)
                {
                    IDWriter.println(id);
                }
    
                for(String position: locationList)
                {
                    locationWriter.println(position);
                }

                IDWriter.close();
                locationWriter.close();
            }

            PrintWriter amountWriter = new PrintWriter(allProductAmountPath);

            for(String eachAmount: amountList)
            {
                amountWriter.println(eachAmount);
            }

            amountWriter.close();

            return resultAmount;
        }
    }

    // Return true if string contain only digits
    public static Boolean isNumeric(String string)
    {
        return string.matches("[0-9]+");
    }

    // Return true if amount is in range
    public static Boolean isInRange(String amount, DefaultTableModel tableModel ,int[] selectedRows)
    {
        Boolean result = true;
        for(int i = 0; i < selectedRows.length; i++)
        {
            int enterAmount = Integer.parseInt(amount);
            int compareAmount = Integer.parseInt(tableModel.getValueAt(selectedRows[i], 2).toString());

            if(enterAmount > compareAmount)
            {
                result = false;
            }
            if(result == false) return result;
        }
        return result;
    }
}
