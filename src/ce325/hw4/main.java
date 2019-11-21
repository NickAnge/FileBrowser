package ce325.hw4;

public class main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                fileBrowser browserPage = new fileBrowser();
            }
        });
    }
}
