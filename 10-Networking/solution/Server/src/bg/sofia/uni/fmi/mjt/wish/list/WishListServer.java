package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.command.CommandCreator;
import bg.sofia.uni.fmi.mjt.wish.list.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.wish.list.storage.ServerStorage;

import bg.sofia.uni.fmi.mjt.wish.list.exceptions.InvalidPortException;
import bg.sofia.uni.fmi.mjt.wish.list.exceptions.CreationFailedException;
import bg.sofia.uni.fmi.mjt.wish.list.exceptions.RunningInterruptedException;
import bg.sofia.uni.fmi.mjt.wish.list.exceptions.ClosureFailed;

import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class WishListServer {

    private static final String SERVER_HOST = "localhost";
    private static final String CLIENT_TERMINATED_EXCEPTION_MESSAGE = "Connection reset";
    private final int serverPort;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    private static final int BUFFER_SIZE = 512;
    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private final CommandExecutor commandExecutor;

    private boolean isRunning;


    public WishListServer(int port) {
        if (port < 1024 || port > 65535) {
            throw new InvalidPortException("WishListServer port must be between 1024 and 65535.");
        }
        this.serverPort = port;
        ServerStorage storage = new ServerStorage();
        commandExecutor = new CommandExecutor(storage);
    }

    public void start() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            isRunning = true;

            new Thread(() -> {
                try {
                    run();
                } catch (IOException e) {
                    throw new RunningInterruptedException("Server running was interrupted", e);
                }
            }).start();
        } catch (IOException e) {
            throw new CreationFailedException("Server creation failed", e);
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
            if (selector.isOpen()) {
                selector.wakeup();
            }
            try {
                serverSocketChannel.close();
            } catch (IOException e) {
                throw new ClosureFailed("Closing server failed", e);
            }
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(SERVER_HOST, serverPort));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void run() throws IOException {
        while (isRunning) {
            if (selector.select() == 0) {
                continue;
            }
            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();

            operateSocketChannels(keyIterator);
        }
    }

    private void operateSocketChannels(Iterator<SelectionKey> keyIterator) throws IOException {
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();

                String clientInput = null;
                try {
                    clientInput = readFromClient(clientChannel);
                } catch (SocketException e) {
                    if (e.getMessage().equals(CLIENT_TERMINATED_EXCEPTION_MESSAGE)) {
                        clientChannel.close();
                        continue;
                    }
                }
                if (clientInput == null) {
                    continue;
                }

                String output = commandExecutor.executeForClient(CommandCreator.newCommand(clientInput), clientChannel);
                writeToClient(output, clientChannel);
            } else if (key.isAcceptable()) {
                accept(selector, key);
            }
            keyIterator.remove();
        }
    }

    private String readFromClient(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        if (clientChannel.read(buffer) < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();
        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    private void writeToClient(String msg, SocketChannel clientSocketChannel) throws IOException {
        buffer.clear();
        buffer.put(msg.getBytes());
        buffer.flip();
        clientSocketChannel.write(buffer);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }
}
