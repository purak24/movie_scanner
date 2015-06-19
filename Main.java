package com.company;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.sql.*;

public class Main extends JFrame {
    private JButton selectYourMovieFolderButton;
    private JPanel rootPanel;

    public Main() {
        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectYourMovieFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Connection conn = null;
                Statement stmt = null;
                Statement stmt2 = null;
                Statement stmt3 = null;
                Statement stmt4 = null;
                Statement stmt5 = null;
                try {
                    //Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection("jdbc:mysql://localhost/imdb", "root", "pass");
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new java.io.File("."));
                    chooser.setDialogTitle("choosertitle");
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setAcceptAllFileFilterUsed(false);
                    if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
                        System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
                    } else {
                        System.out.println("No Selection ");
                    }
                    String temp;
                    temp = chooser.getSelectedFile().toString();
                    File f = new File(temp);
                    String movnamearr[] = new String[1000];
                    int index = 0;
                    String[] pattern_to_strip = {
                            "\\[.*\\]",
                            "\\(.*\\)",
                            //".",
                            "720p",
                            "1080p",
                            "x264",
                            "[1|2][9|0]\\d{2}",
                            "dvd\\.?rip",
                            "xvid",
                            "CD\\d+",
                            "-axxo",
                            "mp3",
                            "hdtv",
                            "divx",
                            "sharkboy",
                            "\\d{0,2}fps",
                            "\\d{0,3}kbps",
                            "hddvd",
                            "torr?ent.",
                            "extended",
                            "WunSeeDee",
                            "\\d{3,4}p",
                            "Part\\d",
                            "-",
                            "YIFY",
                            "x264",
                            "BluRay",
                            "mp4",
                            "avi",
                            "mkv",
                            "m4v",
                            "flv",
                            "vob",
                            "mpg",
                            "SPARKS",
                            "mpeg",
                            "BrRip",
                            "\\.\\.+"
                    };
                    for (File tempfile : f.listFiles()) {
                        if (tempfile.isDirectory())
                            System.out.println("Directory contains sub-directory. Cannot Operate on " + temp);
                        else {
                            String curmoviename = new String();
                            curmoviename = tempfile.getName();
                            System.out.println(curmoviename);

                            if (curmoviename.endsWith(".mov") || curmoviename.endsWith(".avi") || curmoviename.endsWith(".mpg") || curmoviename.endsWith(".mpeg") || curmoviename.endsWith(".mp4") || curmoviename.endsWith(".vob") || curmoviename.endsWith(".flv") || curmoviename.endsWith(".mkv") || curmoviename.endsWith(".m4v")) {
                                for (int j = 0; j < pattern_to_strip.length; j++) {
                                    curmoviename = curmoviename.replaceAll(pattern_to_strip[j], "");
                                    curmoviename = curmoviename.replace('.', ' ');
                                    curmoviename = curmoviename.trim();
                                }
                                movnamearr[index++] = curmoviename;
                            }
                        }
                    }

                    System.out.println("Now, files with movie extension");
                    int counter=0;
                    int counterr=0;
                    for (int ctr = 0; ctr < index; ctr++) {

                        System.out.println(movnamearr[ctr]);
                        //STEP 4: Execute a query
                        System.out.println("Creating statement...");
                        stmt = conn.createStatement();
                        stmt2 = conn.createStatement();
                        stmt3 = conn.createStatement();
                        stmt4 = conn.createStatement();
                        stmt5 = conn.createStatement();
                        String sql;
                        sql = "SELECT * FROM title where title ='" + movnamearr[ctr] + "' and kind_id=1 order by production_year desc limit 1";
                        ResultSet rs = stmt.executeQuery(sql);
                        //STEP 5: Extract data from result set
                        int tempcounter=0;
                        while (rs.next()) {
                            //Retrieve by column name
                            String first = rs.getString("title");
                            String second = rs.getString("id");
                            String third = rs.getString("production_year");

                            counterr++;

                            String sql2 = "select * from movie_info_idx where movie_id=" + second + " and info_type_id=101;";
                            ResultSet rs2 = stmt2.executeQuery(sql2);
                            while (rs2.next()) {
                                counter++;
                                String fourth = rs2.getString("info");

                                String sql3="select * from cast_info where movie_id='"+second+"' and nr_order is not null order by nr_order limit 2;";
                                ResultSet rs3 = stmt4.executeQuery(sql3);

                                //Display values

                                System.out.println("\tName: " + first);
                                System.out.println("\tRating: " + fourth);
                                System.out.println("\tYear: " + third);

                                String actor1="";
                                String actor2="";
                                while(rs3.next())
                                {
                                    String fifth = rs3.getString("person_id");
                                    String sql4="select name from name where id='"+fifth+"';";
                                    ResultSet rs4 = stmt5.executeQuery(sql4);

                                    ;


                                    while(rs4.next())
                                    {

                                        if(tempcounter==0)
                                            actor1=rs4.getString("name");
                                        else
                                            actor2=rs4.getString("name");
                                        tempcounter++;
                                        System.out.println("Actor : "+rs4.getString("name"));
                                    }
                                }

                                //System.out.println(actor1+" *"+actor2);

                                String tempinsert="insert into  localStore values("+counter+",'"+first+"','"+third+"','"+fourth+"','"+actor1+"','"+actor2+"');";
                                stmt3.executeUpdate(tempinsert);

                            }


                        }




                    }


                    String sql="select * from localStore order by Rating desc";
                    ResultSet rs = stmt.executeQuery(sql);
                    ResultSetMetaData rsm=rs.getMetaData();
                    int cols=rsm.getColumnCount();
                    String[] sr = {"id", "Title", "Year", "Rating","Actor1","Actor2"};
                    String[][] a=new String [counterr][cols];
                    int j=0;
                    while(rs.next())
                    {
                        for(int i=0;i<cols;i++)
                        {
                            a[j][i]=rs.getString(i+1);
                        }
                        j++;
                    }


                    JTable table=new JTable(a,sr);
                    JFrame r=new JFrame();
                    r.setExtendedState(r.getExtendedState()|JFrame.MAXIMIZED_BOTH);
                    r.add(new JScrollPane(table));
                    r.setLocationRelativeTo(null);
                    r.setVisible(true);
                    r.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    String sqldrop = "delete from localStore;";
                    stmt.executeUpdate(sqldrop);

                    stmt.close();
                    stmt2.close();
                    stmt3.close();
                    stmt4.close();
                    stmt5.close();
                    conn.close();
                } catch (SQLException se) {
                    //Handle errors for JDBC
                    se.printStackTrace();
                }
                new Frame2();
            }

        });
    }

    public Dimension getPreferredSize() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return new Dimension(500,500);
    }

    public static void main(String s[]) {
        JFrame frame = new JFrame("");
        Main panel = new Main();
        frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                }
        );
        panel.setSize(panel.getPreferredSize());
        try{panel.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("/home/prk/Desktop/film-reel-wht00000.png")))));}catch(Exception e){}
        panel.add(panel.selectYourMovieFolderButton);
        panel.setVisible(true);
    }
}