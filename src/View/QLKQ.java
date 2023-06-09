package View;

import ConnectDB.DatabaseManager;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class QLKQ extends JFrame {
    public QLKQ() {
        createUI();
        attachEventHandlers();
    }

    JPanel field_id, field_kw, field_btn, field_txtArea;
    JButton btn_importfile, btn_ranking, btn_search, btn_wonteams;
    JTextField jtf_id, jtf_kw;
    JTextArea txtArea;

    private void createUI()
    {
        setDefaultCloseOperation(3);

        this.setTitle("Quan ly ket qua ICPC");

        // Flow Layout - Left alignment for every JPanel
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);

        this.setLayout(flowLayout);

        // Import data field, [id] is short for [import data]
        field_id = new JPanel();
        field_id.setPreferredSize(new Dimension(550, 30));
        field_id.setLayout(flowLayout);

        JLabel label_id = new JLabel("Import data");
        label_id.setPreferredSize(new Dimension(160, 25));
        field_id.add(label_id);

        jtf_id = new JTextField();
        jtf_id.setPreferredSize(new Dimension(160,25));
        jtf_id.setHorizontalAlignment(SwingConstants.RIGHT);
        jtf_id.setBorder(new LineBorder(Color.BLACK, 1));
        field_id.add(jtf_id);

        btn_importfile = new JButton("Import file");
        btn_importfile.setPreferredSize(new Dimension(160,25));
        field_id.add(btn_importfile);

//        Keyword field, [kw] is short for [keyword]
        field_kw = new JPanel();
        field_kw.setPreferredSize(new Dimension(550,30));
        field_kw.setLayout(flowLayout);

        JLabel label_kw = new JLabel("Keyword");
        label_kw.setPreferredSize(new Dimension(160,25));
        field_kw.add(label_kw);

        jtf_kw = new JTextField();
        jtf_kw.setPreferredSize(new Dimension(325, 25));
        jtf_kw.setHorizontalAlignment(SwingConstants.RIGHT);
        jtf_kw.setBorder(new LineBorder(Color.BLACK, 1));
        field_kw.add(jtf_kw);

        // Buttons Field
        field_btn = new JPanel();
        field_btn.setPreferredSize(new Dimension(550, 30));
        field_btn.setLayout(flowLayout);

        btn_ranking = new JButton("Ranking");
        btn_ranking.setPreferredSize(new Dimension(160,25));
        field_btn.add(btn_ranking);

        btn_search = new JButton("Search");
        btn_search.setPreferredSize(new Dimension(160,25));
        field_btn.add(btn_search);

        btn_wonteams = new JButton("Won teams");
        btn_wonteams.setPreferredSize(new Dimension(160,25));
        field_btn.add(btn_wonteams);

        // Text Area Field
        field_txtArea = new JPanel();
        field_txtArea.setPreferredSize(new Dimension(550, 180));
        field_txtArea.setLayout(flowLayout);

        txtArea = new JTextArea();
        txtArea.setPreferredSize(new Dimension(500, 150));
        txtArea.setBorder(new LineBorder(Color.BLACK, 1));
        field_txtArea.add(txtArea);

        this.add(field_id);
        this.add(field_kw);
        this.add(field_btn);
        this.add(field_txtArea);

        setSize(550, 350);
        setVisible(true);
    }

    private void attachEventHandlers() {
         btn_importfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    DatabaseManager dm = new DatabaseManager();
                    Connection connection = dm.getConnection();

                    FileReader fr = new FileReader(new File("D:\\QUANG\\Hoc tap\\Java\\JavaPreview\\src\\asset\\" + jtf_id.getText()));
                    BufferedReader br = new BufferedReader(fr);

                    String query_clr = "Delete from icpc";
                    Statement statement_clr = connection.createStatement();
                    statement_clr.executeUpdate(query_clr);
                    txtArea.setText("");

                    String query = "Insert into icpc values (?, ?, ?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(query);

                    String line;
                    String[] fields;
                    while ((line = br.readLine()) != null)
                    {
                        fields = line.split(",");
                        statement.setString(1, fields[0]);
                        statement.setString(2, fields[1]);
                        statement.setString(3, fields[2]);
                        statement.setInt(4, Integer.parseInt(fields[3]));
                        statement.setString(5, fields[4]);

                        statement.executeUpdate();

                        txtArea.append(line + "\n");
                    }

                    dm.closeConnection();

                    br.close();
                    fr.close();
                }

                catch (Exception ex) {
                    System.out.println("Action failed!");
                    ex.printStackTrace();
                }

                // Read the .txt file and load data to the database
                // Your code to read the file and perform database operations goes here
//                System.out.println("Loading data from file and saving to the database...");
            }
        });

         btn_ranking.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                try {
                    DatabaseManager dm = new DatabaseManager();
                    Connection connection = dm.getConnection();
                    Statement statement = connection.createStatement();

                    txtArea.setText("");

                    String querysl = "select teamname, uniname,count(distinct(problemid)) as count, sum(min_time) as time from\n" +
                            "(select teamname, uniname, problemid, min(time) as min_time from icpc where result = 'AC' group by teamname, uniname, problemid) as T \n" +
                            "group by teamname,uniname order by count desc, time";

                    ResultSet result = statement.executeQuery(querysl);

                    int rank = 1, problemsolved = -1, time = -1;
                    while(result.next()) {
                        String teamname = result.getString("teamname");
                        String uniname = result.getString("uniname");

                        if (result.getInt("count") == problemsolved && result.getInt("time") == time) {
                            txtArea.append((rank-1) + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                        }

                        else {
                            problemsolved = result.getInt("count");
                            time = result.getInt("time");
                            txtArea.append(rank + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                        }

                        rank += 1;
                    }

                    dm.closeConnection();
                }

                catch (Exception ex) {
                    System.out.println("Action failed!");
                    ex.printStackTrace();
                }
             }
         });

         btn_search.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 try {
                     DatabaseManager dm = new DatabaseManager();
                     Connection connection = dm.getConnection();
                     Statement statement = connection.createStatement();

                     txtArea.setText("");

                     String querysl = "select teamname, uniname,count(distinct(problemid)) as count, sum(min_time) as time from\n" +
                             "(select teamname, uniname, problemid, min(time) as min_time from icpc where result = 'AC' group by teamname, uniname, problemid) as T \n" +
                             "group by teamname,uniname order by count desc, time";

                     ResultSet result = statement.executeQuery(querysl);

                     String searchTxt = jtf_kw.getText();

                     int rank = 1, problemsolved = -1, time = -1;
                     while(result.next()) {
                         String teamname = result.getString("teamname");
                         String uniname = result.getString("uniname");

                         if (!uniname.contains(searchTxt)) {
                             rank += 1;
                             continue;
                         }

                         if (result.getInt("count") == problemsolved && result.getInt("time") == time) {
                             txtArea.append((rank-1) + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                         }

                         else {
                             problemsolved = result.getInt("count");
                             time = result.getInt("time");
                             txtArea.append(rank + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                         }

                         rank += 1;
                     }

                     dm.closeConnection();
                 }

                 catch (Exception ex) {
                     System.out.println("Action failed!");
                     ex.printStackTrace();
                 }
             }
         });

         btn_wonteams.addActionListener(new ActionListener() {
             @Override
             public void actionPerformed(ActionEvent e) {
                 try {
                     DatabaseManager dm = new DatabaseManager();
                     Connection connection = dm.getConnection();
                     Statement statement = connection.createStatement();

                     txtArea.setText("");

                     String querysl = "select teamname, uniname,count(distinct(problemid)) as count, sum(min_time) as time from\n" +
                             "(select teamname, uniname, problemid, min(time) as min_time from icpc where result = 'AC' group by teamname, uniname, problemid) as T \n" +
                             "group by teamname,uniname order by count desc, time";

                     ResultSet result = statement.executeQuery(querysl);

                     int rank = 1, problemsolved = -1, time = -1;
                     Map<String, Boolean> mp = new HashMap<String, Boolean>();
                     while(result.next()) {
                         String teamname = result.getString("teamname");
                         String uniname = result.getString("uniname");

                        if (mp.get(uniname) != null) {
                            rank += 1;
                            continue;
                        }

                        mp.put(uniname,true);

                         if (result.getInt("count") == problemsolved && result.getInt("time") == time) {
                             txtArea.append((rank-1) + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                         }

                         else {
                             problemsolved = result.getInt("count");
                             time = result.getInt("time");
                             txtArea.append(rank + "," + teamname + "," + uniname + "," + problemsolved + "," + time + "\n");
                         }

                         rank += 1;
                     }

                     dm.closeConnection();
                 }

                 catch (Exception ex) {
                     System.out.println("Action failed!");
                     ex.printStackTrace();
                 }
             }
         });
    }
}
