/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package crazyballrun;

import crazyballrun.game.Game;
import java.net.ServerSocket;
import java.net.Socket;

//class ServerThread extends Thread
//{
//    private Socket [] mClients = new Socket[4];
//    private ServerSocket mServer;
//    private int mClientNumber = 0;
//
//    public ServerThread (ServerSocket server)
//    {
//        mServer = server;
//    }
//
//    public synchronized void setClientNumber (int num, Socket client)
//    {
//        mClientNumber = num + 1;
//        mClients[num] = client;
//    }
//
//    public synchronized int getClientNumber ()
//    {
//        return mClientNumber;
//    }
//
//    @Override
//    public void run () 
//    {
//        while (true)
//        {
//            try
//            {
//                System.out.println("server thread ...");
//                Thread.currentThread().sleep(1000);
//                final int clients = getClientNumber();
//                for (int i = 0; i < clients; i++)
//                {
//                    if (mClients[i].isConnected())
//                    {
//                        System.out.println("client " + i);
//                    }
//                }
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }
//}
//
//class Server extends Thread
//{
//    private Socket [] mClients = new Socket[4];
//    private ServerSocket mServer;
//
//    @Override
//    public void run ()
//    {
//        System.out.println("Hallo, ich bin der Server :) ");
//        try
//        {
//            mServer = new ServerSocket(2000);
//            ServerThread thread = new ServerThread(mServer);
//            thread.start();
//            for (int i = 0; i < 4; i++)
//            {
//                System.out.println("Accept client ...");
//                mClients[i] = mServer.accept();
//                thread.setClientNumber(i, mClients[i]);
//            }
//
//            thread.join();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}
//
//class Client extends Thread
//{
//    public Client () 
//    {
//
//    }
//
//    @Override
//    public void run () 
//    {
//        try
//        {
//            Socket vMe = new Socket("localhost", 2000);
//            Thread.currentThread().sleep(1000);
//            vMe.close();
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//}

/**
 * Create and starts Crazy Ballrun!
 * @author Timm Hoffmeister
 */
public class CrazyBallrun {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
//        Client vClient1 = new Client();
//        Client vClient2 = new Client();
//        Client vClient3 = new Client();
//        Client vClient4 = new Client();
//        Server vServer = new Server();
//        vServer.start();
//        vClient1.start();
//        vClient2.start();
//        vClient3.start();
//        vClient4.start();
//        
//        try
//        {
//            Thread.currentThread().sleep(10000);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
        
        Game.getInstance().startGame();
    }


}
