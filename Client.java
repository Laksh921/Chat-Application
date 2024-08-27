import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class Client extends JFrame {
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Client Messenger");
    private JTextArea messageArea = new JTextArea();
    private PlaceholderTextField messageInput = new PlaceholderTextField("Enter your message here...");
    private Font font = new Font("SansSerif", Font.PLAIN, 20);

    // Modern colors
    private java.awt.Color headingColor = new java.awt.Color(0, 0, 0); // Black
    private java.awt.Color messageAreaColor = new java.awt.Color(0, 0, 0); // Black
    private java.awt.Color inputAreaColor = new java.awt.Color(0, 0, 0); // Black
    private java.awt.Color textColor = new java.awt.Color(0, 255, 0); // Green
    private java.awt.Color borderColor = new java.awt.Color(0, 255, 0); // Green Border
    private java.awt.Color placeholderColor = new java.awt.Color(169, 169, 169); // Gray

    public Client() {
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 8797);
            System.out.println("Connection done!");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("Client Messenger");
        this.setSize(500, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setting up the heading
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        heading.setBackground(headingColor);
        heading.setForeground(textColor); // Change text color to green
        heading.setOpaque(true);

        // Setting up the message area with padding
        messageArea.setFont(font);
        messageArea.setBackground(messageAreaColor);
        messageArea.setForeground(textColor); // Change text color to green
        messageArea.setEditable(false);
        messageArea.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding inside the text area

        // Setting up the message input area with updated border and padding
        messageInput.setFont(font);
        messageInput.setBackground(inputAreaColor);
        messageInput.setForeground(textColor); // Change text color to green
        messageInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1), // Green border
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Padding inside the input field

        // Adding a scroll pane for the message area
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border around the scroll pane

        // Adding components to the frame
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.getContentPane().setBackground(Color.BLACK); // Set background color to black

        this.setVisible(true);
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }
        });
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("Exit")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Server: " + msg + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public static void main(String[] args) {
        System.out.println("This is client...");
        new Client();
    }

    // Custom JTextField with placeholder
    class PlaceholderTextField extends JTextField {
        private String placeholder;
        private java.awt.Color placeholderColor;

        public PlaceholderTextField(String placeholder) {
            this.placeholder = placeholder;
            this.placeholderColor = new java.awt.Color(169, 169, 169); // Gray
            setForeground(placeholderColor);
            setText(placeholder);
            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(textColor); // Text color when typing
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setForeground(placeholderColor);
                        setText(placeholder);
                    }
                }
            });
        }
    }
}
