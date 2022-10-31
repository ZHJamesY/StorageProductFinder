import javax.swing.table.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.*;

public class AddProduct 
{
    private static String tasksPath = "ProductData/Tasks.txt";
    private static String allProductNamePath = "ProductData/AllProducts/Name.txt";
    private static String allProductIDPath = "ProductData/AllProducts/ID.txt";
    private static String allProductLocationPath = "ProductData/AllProducts/Location.txt";
    private static String allProductAmountPath = "ProductData/AllProducts/Amount.txt";

    // Return true if string is numeric
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

    // Initialize table task list
    public static void initializeTaskList(DefaultTableModel model) throws IOException
    {
        makeFile(tasksPath);

        List<String> taskList = new ArrayList<String>();
        taskList = Files.readAllLines(Paths.get(tasksPath));
        
        // Receive data from txt file and set table's value
        for(int i = 0; i < taskList.size(); i++)
        {
            model.addRow(new Object[]{taskList.get(i).substring(0, taskList.get(i).indexOf(",")), taskList.get(i).substring(taskList.get(i).indexOf(",") + 1, taskList.get(i).lastIndexOf(",")), taskList.get(i).substring(taskList.get(i).lastIndexOf(",") + 1, taskList.get(i).length())});
        }
    }

    // Add task to table
    public static void addTaskList(DefaultTableModel model)
    {
        model.addRow(new Object[]{CreateProduct.globalID, CreateProduct.globalName, CreateProduct.globalAmount});
    }

    // Write to Name.txt, ID.txt, Location.txt, Amount.txt, return -1 for store all amount
    public static int createAllProducts(String ID, String text, String location, String amount) throws IOException, InterruptedException
    {
        int result = 0;

        // check if amountText is empty and set result = -1
        if(amount == "")
        {
            List<String> taskList = new ArrayList<String>();
            taskList = Files.readAllLines(Paths.get(tasksPath));
            amount = taskList.get(0).substring(taskList.get(0).lastIndexOf(",") + 1, taskList.get(0).length());
            result = -1;
        }

        List<String> nameList = new ArrayList<String>();
        List<String> IDList = new ArrayList<String>();
        List<String> locationList = new ArrayList<String>();
        List<String> amountList = new ArrayList<String>();


        // If no such file or directory, create
        makeDir();
        makeFile(allProductNamePath);
        makeFile(allProductIDPath); 
        makeFile(allProductLocationPath);
        makeFile(allProductAmountPath);
        
        // Assign file text to Lists
        nameList = Files.readAllLines(Paths.get(allProductNamePath));
        IDList = Files.readAllLines(Paths.get(allProductIDPath));
        locationList = Files.readAllLines(Paths.get(allProductLocationPath));
        amountList = Files.readAllLines(Paths.get(allProductAmountPath));

        if(nameList.isEmpty())
        {
            PrintWriter nameWriter = new PrintWriter(allProductNamePath);
            PrintWriter IDWriter = new PrintWriter(allProductIDPath);
            PrintWriter locationWriter = new PrintWriter(allProductLocationPath);
            PrintWriter amountWriter = new PrintWriter(allProductAmountPath);


            nameWriter.println(text);
            IDWriter.println(ID + ",");
            locationWriter.println(location + ",");
            amountWriter.println(amount + ",");

            nameWriter.close();
            IDWriter.close();
            locationWriter.close();
            amountWriter.close();
        }
        else
        {
            int index = CreateProduct.binarySearch(nameList, text, 0, nameList.size(), 0);

            if(index < nameList.size() && index >= 0) // when inserting new product into Name.txt, between two product
            {
                nameList.add(index, text);
                IDList.add(index, ID + ",");
                locationList.add(index, location + ",");
                amountList.add(index, amount + ",");

            }
            else if(index == nameList.size()) // when adding new product at the end of Name.txt
            {
                nameList.add(text);
                IDList.add(ID + ",");
                locationList.add(location + ",");
                amountList.add(amount + ",");

            }
            else if(index < 0) // when adding same product
            {
                int insertIndex = -1;

                ArrayList<String> IDArrayList = new ArrayList<>(SearchProduct.strToArrayList(IDList.get((index * (-1)) -1)));
                ArrayList<String> locationArrayList = new ArrayList<>(SearchProduct.strToArrayList(locationList.get((index * (-1)) -1)));
                ArrayList<String> amountArrayList = new ArrayList<>(SearchProduct.strToArrayList(amountList.get((index * (-1)) -1)));

                for(int i = 0; i < IDArrayList.size(); i++)
                {
                    // Get integer of year, month, day
                    int addYear = Integer.parseInt(ID.substring(4, 5 + 1));
                    int listYear = Integer.parseInt(IDArrayList.get(i).substring(4, 5 + 1));
                    int addMonth = Integer.parseInt(ID.substring(2, 3 + 1));
                    int listMonth = Integer.parseInt(IDArrayList.get(i).substring(2, 3 + 1));
                    int addDay = Integer.parseInt(ID.substring(0, 1 + 1));
                    int listDay = Integer.parseInt(IDArrayList.get(i).substring(0, 1 + 1));

                    // Compare year, month, day, if >, insertIndex + 1
                    if(addYear > listYear)
                    {
                        insertIndex = i;
                    }
                    else if(addMonth > listMonth && addYear == listYear)
                    {
                        insertIndex = i;
                    }
                    else if(addDay > listDay && addYear == listYear && addMonth == listMonth)
                    {
                        insertIndex = i;
                    }
                }

                // Add value at string's end
                if(insertIndex + 1 == IDArrayList.size())
                {
                    IDList.set((index * (-1)) -1, IDList.get((index * (-1)) -1) + ID + ",");  
                    locationList.set((index * (-1)) -1, locationList.get((index * (-1)) -1) + location + ",");
                    amountList.set((index * (-1)) -1, amountList.get((index * (-1)) -1) + amount + ",");
                }
                // Insert value at index: insertIndex + 1, edit ArrayList for desire string
                else
                {
                    IDArrayList.add(insertIndex + 1, ID);
                    locationArrayList.add(insertIndex + 1, location);
                    amountArrayList.add(insertIndex + 1, amount);

                    IDList.set((index * (-1)) -1, IDArrayList.toString().substring(1, IDArrayList.toString().length() - 1).replace(", ", ",") + ",");
                    locationList.set((index * (-1)) -1, locationArrayList.toString().substring(1, locationArrayList.toString().length() - 1).replace(", ", ",") + ",");
                    amountList.set((index * (-1)) -1, amountArrayList.toString().substring(1, amountArrayList.toString().length() - 1).replace(", ", ",") + ",");
                }
            }

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
        }
        return result;
    }

    // Make directory
    public static void makeDir()
    {
        File directory = new File("ProductData/AllProducts");
        if(!directory.exists())
        {
            directory.mkdir();
        }
    }

    // Make File
    public static void makeFile(String filePath) throws IOException
    {
        File file = new File(filePath);
        if(!file.exists()) 
        { 
            Path newFilePath = Paths.get(filePath);
            Files.createFile(newFilePath);
        }
    }

    // Generate ID
    public static String generateID(int index, String ID) throws IOException
    {
        List<String> IDList = new ArrayList<String>();
        IDList = Files.readAllLines(Paths.get(allProductIDPath));

        String result = randomID(ID);
        if(index != -1)
        {

            while(IDList.get(index).contains(result))
            {
                result = randomID(ID);
            }
        }
        return result + ",";
    }

    // Random ID
    public static String randomID(String ID)
    {
        Random rnd = new Random();
        char randomLetter = (char) ('A' + rnd.nextInt(26));
        char randomNum1 = (char) ('0' + rnd.nextInt(10));
        char randomNum2 = (char) ('0' + rnd.nextInt(10));

        return ID.replace("/","") + "-" + randomLetter + randomNum1 + randomNum2;
    }

    // Delete Tasks.txt's added task or selected tasks
    public static void deleteAddedTask(int[] index, String addIndex) throws IOException
    {
        List<String> taskList = new ArrayList<String>();
        taskList = Files.readAllLines(Paths.get(tasksPath));

        if(index.length != 0)
        {
            for(int i = 0; i < index.length; i++)
            {
                taskList.remove(index[i]);
            }

            PrintWriter locationWriter = new PrintWriter(tasksPath);

            for(int i = 0; i < taskList.size(); i++)
            {
                locationWriter.println(taskList.get(i));
            }
            locationWriter.close();
        }
        else if(addIndex == "YES")
        {
            taskList.remove(0);
            PrintWriter locationWriter = new PrintWriter(tasksPath);

            for(int i = 0; i < taskList.size(); i++)
            {
                locationWriter.println(taskList.get(i));
            }
            locationWriter.close();
        }
    }

    // Position location
    public static boolean correctFormat(String str)
    {
        return Pattern.matches("...", str); 
    }    

    // Update Tasks.txt file and table's amount
    public static void diffLocationAmount(DefaultTableModel model, String amount) throws IOException
    {
        List<String> taskList = new ArrayList<String>();
        taskList = Files.readAllLines(Paths.get(tasksPath));
        
        int fileAmount = Integer.parseInt(taskList.get(0).substring(taskList.get(0).lastIndexOf(",") + 1, taskList.get(0).length()));
        int newAmount = fileAmount - Integer.parseInt(amount);

        taskList.set(0, taskList.get(0).replace(String.valueOf(fileAmount), String.valueOf(newAmount)));

        PrintWriter taskWriter = new PrintWriter(tasksPath);
        for(String task: taskList)
        {
            taskWriter.println(task);
        }
        taskWriter.close();

        model.setValueAt(String.valueOf(newAmount), 0, 2);
    }
}
