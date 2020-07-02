public class Connection {

    private static Connection INSTANCE;

    private Connection() {
    }

    public static void main(String[] args) {
        Connection currentConnection = Connection.getOrInitConnection();
        currentConnection.send("Hello World!");
    }

    /**
     * Get current connection or init it if it was never created.
     *
     * @return the current connection
     */
    public static Connection getOrInitConnection() {
        if (INSTANCE == null) {
            INSTANCE = new Connection();
        }
        return INSTANCE;
    }

    /**
     * Send a message through the current connection.
     *
     * @param message the message to send
     */
    public void send(String message) {
        System.out.println(message);
    }
}