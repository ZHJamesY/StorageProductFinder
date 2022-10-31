import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.Random;

public class CreateProduct 
{
    private static String tasksPath = "ProductData/Tasks.txt";

    // Data access by add product table list
    public static String globalID;
    public static String globalName;
    public static String globalAmount;

    // Binary search through Name.txt file
    public static int binarySearch(List<String> productList, String text, int index, int upperBound, int lowerBound) throws InterruptedException
    {
        int mid;
        int result = -1;

        mid = ((upperBound + lowerBound) / 2);

        if(mid == 0 && (text.compareTo(productList.get(mid)) == 0))
        {
            return -1;
        }
        else if(mid == 0 && (text.compareTo(productList.get(mid)) < 0))
        {
            return 0;
        }
        else if(mid == productList.size() - 1 && (text.compareTo(productList.get(mid)) != 0) && (text.compareTo(productList.get(mid)) > 0))
        {
            return (mid + 1);
        }
        else if(text.compareTo(productList.get(mid)) == 0)
        {
            return ((mid + 1) * (-1));
        }
        else if(text.compareTo(productList.get(mid - 1)) == 0)
        {
            return ((mid) * (-1));
        }
        else if(text.compareTo(productList.get(mid - 1)) > 0 && text.compareTo(productList.get(mid)) < 0)
        {
            return mid;
        }
        else if(text.compareTo(productList.get(mid)) > 0)
        {
            result = binarySearch(productList, text, mid, upperBound, mid);
        }
        else if(text.compareTo(productList.get(mid - 1)) < 0)
        {
            result = binarySearch(productList, text, mid, mid, lowerBound);
        }
        return result;
    }

    // Write to Tasks.txt file
    public static void createAddToTasks(String ID, String name, String amount) throws IOException
    {
        List<String> taskList = new ArrayList<String>();
        String resultID;

        makeFile(tasksPath);

        taskList = Files.readAllLines(Paths.get(tasksPath));
        resultID = taskListID(taskList, ID);
        taskList.add(resultID + "," + name + "," + amount);
        
        globalID = resultID;
        globalName = name;
        globalAmount = amount;

        PrintWriter taskListWriter = new PrintWriter(tasksPath);

        for(String task: taskList)
        {
            taskListWriter.println(task);
        }
        taskListWriter.close();
    }

    // Generate task list ID
    public static String taskListID(List<String> list, String ID)
    {
        String resultID = "";
        resultID = randomID(ID);
        if(!list.isEmpty())
        {
            for(int i = 0; i < list.size(); i++)
            {
                if(list.get(i).contains(resultID))
                {
                    resultID = randomID(ID);
                }
            }
        }
        return resultID;
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

    // Make file
    public static void makeFile(String filePath) throws IOException
    {
        File file = new File(filePath);
        if(!file.exists()) 
        { 
            Path newFilePath = Paths.get(filePath);
            Files.createFile(newFilePath);
        }
    }
}
