package http.filehandler;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TCPSenderTest {
	private static Logger logger;
	
	@BeforeClass
	public static void prepare() {
		logger = new Logger("");		
	}
	
	@AfterClass
	public static void tearDown() {		
		logger.deleteLogFile();
	}

	@Test
	public void testTCPSenderWithoutAnyParameter() {
        final Socket socket = mock(Socket.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
			when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
		} catch (IOException e) {			
			e.printStackTrace();
		}

        TCPSender sender = new TCPSender(logger, 0) {
            @Override
            protected Socket createSocket() {
                return socket;
            }
        };
        Assert.assertTrue("Message sent successfully", sender.call()==0);
        Assert.assertTrue(byteArrayOutputStream.toString().equals(""));
	}
	
	@Test
	public void testTCPSenderWithSocketParameter() {
        final Socket socket = mock(Socket.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
			when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
		} catch (IOException e) {			
			e.printStackTrace();
		}

        TCPSender sender = new TCPSender(logger, 0) {
            @Override
            protected Socket createSocket() {
                return socket;
            }
        };

        sender.setReceiverParameters(42, "1.1.1.1");
        Assert.assertTrue("Message sent successfully", sender.call()==0);
        Assert.assertTrue(byteArrayOutputStream.toString().equals(""));
	}

	@Test
	public void testTCPSendAFileInOnePacket() {
        final Socket socket = mock(Socket.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
			when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
		} catch (IOException e) {			
			e.printStackTrace();
		}

        TCPSender sender = new TCPSender(logger, 0) {
            @Override
            protected Socket createSocket() {
                return socket;
            }
        };

        sender.setReceiverParameters(42, "1.1.1.1");
        FileInstance file =  new FileInstance(logger, "test.txt");
        file.SplitFileToPockets(30);
        sender.setFile(file);
        Assert.assertTrue("Message sent successfully", sender.call()==1);
        String testString = new String("POST test.txt HTTP*/1.0\nID: 0\nTEXT: 123456789asdfghjkyxcvbnm\nEND_PACKET\r\nEND\r\n");
        String out = byteArrayOutputStream.toString(); 
        Assert.assertTrue(out.equals(testString));
	}

	@Test
	public void testTCPSendAFileInThreePacket() {
        final Socket socket = mock(Socket.class);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
			when(socket.getOutputStream()).thenReturn(byteArrayOutputStream);
		} catch (IOException e) {			
			e.printStackTrace();
		}

        TCPSender sender = new TCPSender(logger, 0) {
            @Override
            protected Socket createSocket() {
                return socket;
            }
        };

        sender.setReceiverParameters(42, "1.1.1.1");
        FileInstance file =  new FileInstance(logger, "test.txt");
        file.SplitFileToPockets(10);
        sender.setFile(file);
        Assert.assertTrue("Message sent successfully", sender.call()==3);
        StringBuffer testString = new StringBuffer("POST test.txt HTTP*/1.0\nID: 0\nTEXT: 123456789a\nEND_PACKET\r\n");
        testString.append("POST test.txt HTTP*/1.0\nID: 1\nTEXT: sdfghjkyxc\nEND_PACKET\r\n");
        testString.append("POST test.txt HTTP*/1.0\nID: 2\nTEXT: vbnm\nEND_PACKET\r\n");        
        testString.append("END\r\n");
        
        String out = byteArrayOutputStream.toString(); 
        Assert.assertTrue(out.equals(testString.toString()));
	}

}
