using Assets.Scripts;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using UnityEngine;

public class TCP_IO : CommunicationLayer
{

    #region private members
    private TcpListener serverSocket;
    private TcpListener dataSocket;
    private Socket clientSocket;
    private Socket data_connection;
    private IPAddress ipAd = IPAddress.Parse("192.168.137.1");
    //private IPAddress ipAd = IPAddress.Loopback;
    private const int port = 8099;
    private const int data_port = 8199;
    private int connectedClients = 0;
    private NetworkStream clientStream;
    private NetworkStream dataStream;
    #endregion

    #region methods
    /// <summary>
    /// Starts the server waiting for a connection from the raspberry pi client
    /// </summary>
    public void Init()
    {
        Thread t = new Thread(() => {
            try
            {
                serverSocket = new TcpListener(ipAd, port);
                serverSocket.Start();
                Debug.Log("Client Server running at port " + port + "...");
                Debug.Log("The local End point is  :" + serverSocket.LocalEndpoint);
                Debug.Log("Waiting for a connection.....");
                clientSocket = serverSocket.AcceptSocket();
                Debug.Log("Connection accepted from " + clientSocket.RemoteEndPoint);
                clientStream = new NetworkStream(clientSocket, true);
                Debug.Log(" CONNECTED ");
            }
            catch (Exception e)
            {
                Debug.Log("Error..... " + e.StackTrace);
            }
        });

        Thread t1 = new Thread(() =>
        {
            try
            {
                dataSocket = new TcpListener(ipAd, data_port);
                dataSocket.Start();
                Debug.Log("Data Server running at port " + port + "...");
                Debug.Log("The local End point is  :" + dataSocket.LocalEndpoint);
                Debug.Log("Waiting for a connection.....");
                data_connection = dataSocket.AcceptSocket();
                Debug.Log("Connection accepted from " + data_connection.RemoteEndPoint);
                dataStream = new NetworkStream(data_connection, true);
                Debug.Log(" CONNECTED ");
            }
            catch (Exception e)
            {
                Debug.Log("Error..... " + e.StackTrace);
            }
        });

        t.Start();
        //t1.Start(); //datasocket is disabled
    }

    //closes the server
    public void Close()
    {
        clientSocket.Close();
        serverSocket.Stop();
    }

    //validates responses sent from the client to ensure message integrity
    private Boolean validateResponse(string response)
    {
        return response == "OK";
    }

    /// <summary>
    /// Sends a string data by converting to byte message and sending to client.
    /// </summary>
    /// <param name="data">message to send</param>
    /// <returns>true if successful</returns>
    public bool Send(params object[] data)
    {
        Byte[] sendBytes;
        string Message = data[0] + "\r\n";
        int MessageLength = Message.Length;
        sendBytes = Encoding.ASCII.GetBytes(Message);
        try
        {
            if (clientStream.CanWrite)
            {
                clientStream.Write(sendBytes, 0, sendBytes.Length);
                clientStream.Flush();
            }
        }
        catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    /// <summary>
    /// Waits to recieve a byte messages from the client, then returns the resulting message string.
    /// </summary>
    /// <returns>message from client</returns>
    public object Receive()
    {
        byte[] message = new byte[4096];
        int bytesRead;
        bytesRead = 0;
        try
        {
            bytesRead = clientStream.Read(message, 0, 4096);
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.StackTrace);
        }
        if (bytesRead == 0)
        {
            connectedClients -= 1;
            return null;
        }
        ASCIIEncoding encoder = new ASCIIEncoding();
        string msg = encoder.GetString(message, 0, bytesRead);
        return msg;
    }
    #endregion

}
