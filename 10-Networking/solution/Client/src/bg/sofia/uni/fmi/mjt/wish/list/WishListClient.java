package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class WishListClient {

    private static final int SERVER_PORT = 5555;
    private static final String SERVER_HOST = "localhost";
    private static final ByteBuffer buffer = ByteBuffer.allocateDirect(512);

    private static final String INPUT_INDICATOR_SYMBOL = "=> ";
    private static final String COMMAND_BREAK_CONNECTION = "disconnect";

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                System.out.print(INPUT_INDICATOR_SYMBOL);
                String line = scanner.nextLine();

                writeFromBufferToSocketChannel(line, socketChannel);

                String reply = readFromSocketChannelToBuffer(socketChannel);
                System.out.println(reply);

                if (line.equals(COMMAND_BREAK_CONNECTION)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("There is a problem with the network communication. Server might be not running.");
            e.printStackTrace();
        }
    }

    private static void writeFromBufferToSocketChannel(String msg, SocketChannel sc) throws IOException {
        buffer.clear();
        buffer.put(msg.getBytes());
        buffer.flip();
        sc.write(buffer);
    }

    private static String readFromSocketChannelToBuffer(SocketChannel sc) throws IOException {
        buffer.clear();
        sc.read(buffer);
        buffer.flip();

        byte[] byteArray = new byte[buffer.remaining()];
        buffer.get(byteArray);
        return new String(byteArray);
    }
}
