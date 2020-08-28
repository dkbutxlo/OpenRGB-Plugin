package de.lars.openrgbwrapper.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {

    private InetSocketAddress address;
    private Socket socket;
    private DataOutputStream outStream;
    private DataInputStream inStream;

    private boolean connected = false;
    private int timeout = 5000;

    /**
     * Create a new client instance
     * @param hostname  hostname of the server
     * @param port      port of the server
     */
    public Client(String hostname, int port) {
        setConnectionOptions(hostname, port);
    }

    /**
     * Set the client connection options
     * @param hostname  hostname where the client should connect to
     * @param port      port the client should use
     * @return          true if the new connection options could be set,
     *                  false if the client is currently connected and the
     *                  options could not be set
     */
    public boolean setConnectionOptions(String hostname, int port) {
        // can not set new connection options while client is connected
        if(connected) return false;
        address = new InetSocketAddress(hostname, port);
        return true;
    }

    /**
     * Get the connection options of this client
     * @return          hostname and port as InetSocketAddress
     */
    public InetSocketAddress getConnectionOptions() {
        return address;
    }

    /**
     * Return whether the client is connected
     * @return          true if socket is connected,
     *                  false otherwise
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Connects the client to the specified host and port
     * @return              true if the client was not connected and could connect to the server
     * @throws IOException  if an error occurs while connecting
     */
    public synchronized boolean connect() throws IOException {
        // return if client is already connected
        if(isConnected()) return false;
        // set connected state to true, to prevent new socket creation while old socket tries to connect
        connected = true;
        // create new socket
        socket = new Socket();

        try {
            // connect to address
            socket.connect(address, timeout);
            // initialize output and input streams
            outStream = new DataOutputStream(socket.getOutputStream());
            inStream = new DataInputStream(socket.getInputStream());
            return true;
        } catch (IOException e) {
            connected = false;
            if(socket.isConnected())
                socket.close();
            socket = null;
            outStream = null;
            inStream = null;
            throw new IOException("Could not connect to server.", e);
        }
    }

    /**
     * Disconnects the client
     * @return              true if the client was connected and could be closed,
     *                      false otherwise
     * @throws IOException  if an error occurs while closing the socket
     */
    public synchronized boolean disconnect() throws IOException {
        // return if client is not connected
        if(!isConnected() || socket == null) return false;
        connected = false;
        socket.close();
        socket = null;
        outStream = null;
        inStream = null;
        return true;
    }

    /**
     * Set socket connection timeout (default: 5000)
     * @param timeout       timeout in milliseconds for the next connection
     */
    public void setConnectionTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Get the output stream of the current socket connection
     * @return          output stream of the connected socket or
     *                  null if socket is not connected
     */
    public DataOutputStream getOutStream() {
        return outStream;
    }

    /**
     * Get the input stream of the current socket connection
     * @return          input stream of the connected socket or
     *                  null if socket is not connected
     */
    public DataInputStream getInStream() {
        return inStream;
    }
}
