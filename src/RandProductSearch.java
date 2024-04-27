import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {
    private JLabel searchLabel, resultLabel;
    private JTextField searchTextField;
    private JTextArea resultTextArea;
    private JButton searchButton, quitButton;
    private RandomAccessFile productFile;

    public RandProductSearch() throws IOException {
        super("Product Search");

        searchLabel = new JLabel("Enter partial product name:");
        resultLabel = new JLabel("Search Results:");
        searchTextField = new JTextField(20);
        resultTextArea = new JTextArea(10, 40);
        searchButton = new JButton("Search Products");
        quitButton = new JButton("Quit");

        setLayout(new BorderLayout());
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchLabel);
        searchPanel.add(searchTextField);
        searchPanel.add(searchButton);
        searchPanel.add(quitButton);
        add(searchPanel, BorderLayout.NORTH);
        add(resultLabel, BorderLayout.CENTER);
        add(new JScrollPane(resultTextArea), BorderLayout.SOUTH);

        searchButton.addActionListener(e -> searchProducts());

        quitButton.addActionListener(e -> {
            closeFile();
            System.exit(0);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void searchProducts() {
        String partialName = searchTextField.getText();
        try {
            if (productFile == null) {
                productFile = new RandomAccessFile("data.dat", "r");
            }

            resultTextArea.setText("");

            productFile.seek(0);

            while (true) {
                Product product = readNextRecord();

                if (product == null) {
                    break;
                }

                if (product.getName().contains(partialName)) {
                    resultTextArea.append(product.toCSVDataRecord() + "\n");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while reading from the file.");
        } finally {
            closeFile();
        }
    }

    private Product readNextRecord() throws IOException {
        String id = productFile.readUTF();
        String name = productFile.readUTF();
        String description = productFile.readUTF();
        double cost = productFile.readDouble();

        return new Product(id, name, description, cost);
    }

    private void closeFile() {
        try {
            if (productFile != null) {
                productFile.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new RandProductSearch();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
