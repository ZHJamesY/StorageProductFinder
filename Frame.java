import javax.swing.*;
import java.io.File;
import java.io.IOException;
// import java.util.concurrent.TimeUnit; //TimeUnit.SECONDS.sleep(1);

public class Frame 
{
    private static JFrame frame = new JFrame();

    public static void mainFrame() throws IOException
    {
        // create Product Data directory
        makeDir();

        frame.setSize(700, 600);
        frame.setTitle("Product Find");
        frame.setLocation(480, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // add tabs
        frame.add(allTabs());

        frame.setVisible(true);
    }

    // Panels
    public static JTabbedPane allTabs() throws IOException
    {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(45, 15, 600, 530);
        tabbedPane.addTab("Create Product", null, CreateTabPanel.createPanel(), null);
        tabbedPane.addTab("Add Product", null, AddTabPanel.addPanel(), null);
        tabbedPane.addTab("Search Product", null, SearchTabPanel.findPanel(), null);
        return tabbedPane;
    }

    // make directory
    public static void makeDir()
    {
        File directory = new File("ProductData");
        if(!directory.exists())
        {
            directory.mkdir();
        }
    }
}
