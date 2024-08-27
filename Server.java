import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private JTextArea textArea = new JTextArea();
    private PlaceholderTextField textInput = new PlaceholderTextField("Enter your message here...");
    private Font font = new Font("Arial", Font.PLAIN, 18);

    // Different colors from the client
    private java.awt.Color headingColor = new java.awt.Color(0, 102, 204); // Dark Blue
    private java.awt.Color textAreaColor = new java.awt.Color(240, 240, 240); // Light Gray
    private java.awt.Color inputAreaColor = new java.awt.Color(255, 255, 255); // White
    private java.awt.Color textColor = new java.awt.Color(0, 51, 102); // Navy Blue
    private java.awt.Color borderColor = new java.awt.Color(150, 150, 150); // Medium Gray
    private java.awt.Color placeholderColor = new java.awt.Color(169, 169, 169); // Gray

    public Server() {
        try {
            server = new ServerSocket(8797);
            System.out.println("Server started... Listening for a connection");
            socket = server.accept();
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            createGUI();
            handleEvents();
            startReading();
            startWriting(); // Uncomment to allow console input
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI() {
        this.setTitle("Server Messenger");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        // Set the background color of the entire frame to blue
        this.getContentPane().setBackground(new java.awt.Color(0, 0, 255)); // Blue
    
        // Setting up the heading
        heading.setFont(font);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        heading.setBackground(headingColor);
        heading.setForeground(Color.BLACK); // White text on dark blue background
        heading.setOpaque(true);
    
        // Setting up the text area with padding and a different border
        textArea.setFont(font);
        textArea.setBackground(textAreaColor);
        textArea.setForeground(textColor);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding inside the text area
    
        // Setting up the text input area with a different border style
        textInput.setFont(font);
        textInput.setBackground(inputAreaColor);
        textInput.setForeground(textColor);
        textInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2), // Slightly thicker border
                BorderFactory.createEmptyBorder(10, 10, 10, 10))); // Padding inside the input field
    
        // Adding a scroll pane for the text area
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // No border around the scroll pane
    
        // Adding components to the frame
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.add(textInput, BorderLayout.SOUTH);
    
        this.setVisible(true);
    }
    

    private void handleEvents() {
        textInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String contentToSend = textInput.getText();
                    textArea.append("Server: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    textInput.setText("");
                    textInput.requestFocus();
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
                        System.out.println("Client terminated the chat");
                        JOptionPane.showMessageDialog(this, "Client terminated the chat");
                        textInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    textArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                while (true && !socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("Exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r2).start();
    }

    public static void main(String[] args) {
        System.out.println("This is Server... starting Server");
        new Server();
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
