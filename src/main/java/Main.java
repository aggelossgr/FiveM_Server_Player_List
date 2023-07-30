import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import org.json.JSONArray;

import javax.swing.*;


public class Main {
    String url;
    JFrame frame;
    JTable table;
    JScrollPane scrollPane;
    String[][] data;

    public Main(String url) throws MalformedURLException {
        this.url = url;
        frame = initWindow();
        String[][] array = this.formatData();
        Arrays.sort(array, Comparator.comparing(a -> a[0]));
        String[] names = {"NAME", "ID", "DISCORD_ID", "STEAM_ID"};
        table = new JTable(array, names) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            ;
        };
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.createVerticalScrollBar();
        frame.add(scrollPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("thumbnail.png")));
    }

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        while (true) {
            JFrame frame = new JFrame("Fivem List");
            JTextField textField = new JTextField();
            JButton submit = new JButton("Submit");
            JLabel label = new JLabel("Enter Server IP Here");
            JLabel label1 = new JLabel("ess");
            submit.setBounds(100, 130, 200, 30);
            label.setBounds(130, 70, 200, 30);
            textField.setBounds(100, 100, 200, 30);
            frame.add(textField);
            frame.add(label);
            frame.add(submit);
            frame.setSize(400, 300);
            frame.setLayout(null);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("thumbnail.png")));
            submit.addActionListener(e -> {
                if (e.getActionCommand().equals("Submit")) {
                    try {
                        new Main("http://" + textField.getText() + "/players.json");
                        frame.setVisible(false);
                    } catch (MalformedURLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            break;
        }
    }

    private String stream(URL url) {
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONArray pingServer(String sUrl) throws MalformedURLException {
        URL url = new URL(sUrl);
        String result = stream(url);
        return new JSONArray(result);
    }

    private String[][] formatData() throws MalformedURLException {
        JSONArray obj = this.pingServer(url);
        data = new String[obj.length()][4];
        for (int i = 0; i < obj.length(); i++) {
            data[i][0] = obj.getJSONObject(i).getString("name");
            data[i][1] = String.valueOf(obj.getJSONObject(i).getInt("id"));
            JSONArray identifiers = obj.getJSONObject(i).getJSONArray("identifiers");
            for (int j = 0; j < identifiers.length(); j++) {
                if (identifiers.getString(j).toLowerCase().contains("discord")) {
                    data[i][2] = identifiers.getString(j).substring(8);
                } else if (identifiers.getString(j).toLowerCase().contains("steam")) {
                    BigInteger integer = new BigInteger(identifiers.getString(j).substring(6), 16);
                    data[i][3] = String.valueOf(integer);
                }
            }
        }
        return data;
    }

    private JFrame initWindow() {
        JFrame frame = new JFrame("Fivem Server Player List");
        frame.setVisible(true);
        return frame;
    }
}