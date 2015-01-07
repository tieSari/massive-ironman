import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTConnector;

public class RobotUI {
	public static void main(String[] args) {
		boolean repeat = true;
		System.out.println("Sonarin mittaustulokset:");
		while (repeat) {
			boolean connected = false;
			try {
				NXTConnector conn = new NXTConnector();

				for (int i = 0; i < 60 && !connected; i++) {
					connected = conn.connectTo("btspp://");
					if(!connected)
					Thread.sleep(500);
				}
				if (!connected) {
					System.err.println("Failed to connect to any NXT");
					repeat = false;
					continue;
				}
				DataInputStream dis = conn.getDataIn();
				dis = conn.getDataIn();
				int indeksi = 0;
				while (indeksi < 8) {

					int arvo = dis.readInt();
					// System.out.println("Received " + arvo);
					for (int i = 0; i < arvo; i++) {
						System.out.print("*");
					}
					indeksi++;
					if (arvo > 0)
						System.out.println("");
				}
				System.out.println("");
				dis.close();
				conn.close();
				conn = null;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
	}
}

