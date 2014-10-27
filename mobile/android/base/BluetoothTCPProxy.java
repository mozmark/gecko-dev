package org.mozilla.gecko;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class BluetoothTCPProxy {
    private static BluetoothTCPProxy instance;
    private int port = 5432;
    private boolean running = false;
    private ServerSocket serverSocket;
	protected BluetoothAdapter mAdapter;
	protected BluetoothDevice mDevice;
    
    private BluetoothTCPProxy(){
    }
    
    public void startServer() {
        Thread t = new Thread(new Runnable() {
            @SuppressLint("NewApi") @Override
            public void run() {
                Log.w("BluetoothProxy","**** Starting! ***********");
                mAdapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = mAdapter.getRemoteDevice("00:13:04:11:16:90");
                try {
                    serverSocket = new ServerSocket(port);
                    running = true;
                    while(running) {
                        Socket s = serverSocket.accept();
                        Connector connector = new Connector(device, s);
                        connector.connect();
                        Log.w("BluetoothProxy","**** ACCEPTED! "+s.toString());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    
    public void stopServer() {
        this.running = false;
        if (null!=serverSocket) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverSocket = null;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public static BluetoothTCPProxy getInstance() {
        if (null == instance) {
            instance = new BluetoothTCPProxy();
        }
        return instance;
    }
    
    public static void ensureRunning(){ 
        BluetoothTCPProxy proxy = BluetoothTCPProxy.getInstance();
        if(!proxy.isRunning()){
            proxy.startServer();
        }
    }
}

class Connector {
    private final BluetoothSocket mmBtSocket;
    private final BluetoothDevice mmBtDevice;
    private final Socket mmSocket;

    public Connector(BluetoothDevice device, Socket s) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmBtDevice = device;
        mmSocket = s;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"));
        } catch (IOException e) { }
        mmBtSocket = tmp;
    }

    public void connect() {
        // Cancel discovery because it will slow down the connection
        //MyService.mBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmBtSocket.connect();
            Thread up = new Thread(new Runnable(){
				public void run() {
					try {
						InputStream in = mmSocket.getInputStream();
						OutputStream out = mmBtSocket.getOutputStream();
						byte[] buf = new byte[512];
						for (int read = 0; -1 != read; read = in.read(buf)) {
							out.write(buf, 0, read);
						}
					} catch (IOException e) {
					} finally {
						try {
							mmSocket.close();
						} catch (IOException e1) {
						}
						try {
							mmBtSocket.close();
						} catch (IOException e1) {
						}
					}
				}
            });
            Thread down = new Thread(new Runnable(){
				public void run() {
					try {
						InputStream in = mmBtSocket.getInputStream();
						OutputStream out = mmSocket.getOutputStream();
						byte[] buf = new byte[512];
						for (int read = 0; -1 != read; read = in.read(buf)) {
							out.write(buf, 0, read);
						}
					} catch (IOException e) {
					} finally {
						try {
							mmSocket.close();
						} catch (IOException e1) {
						}
						try {
							mmBtSocket.close();
						} catch (IOException e1) {
						}
					}
				}
            });
            up.start();
            down.start();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmBtSocket.close();
            } catch (IOException closeException) { }
            return;
        }
    }

    public void cancel() {
        try {
            mmBtSocket.close();
        } catch (IOException e) { }
    }
}