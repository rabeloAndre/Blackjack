package com.mycompany.blackjack;

import java.util.Random;
import java.io.*;
import java.net.*;

public class GameServer {
    
    private ServerSocket ss;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int numPlayers;
    private int numbermagic;
    private int player1ButtonNum;
    private int player2ButtonNum;
    
    Random random = new Random();
    
    public GameServer(){
        System.out.println("------GAME SERVER------");
        numPlayers = 0;
        numbermagic = 0;
        try{
            ss = new ServerSocket(30000);
            
        }catch(IOException ex){
            System.out.println("IOException from GameServer Constructor");
            
        }
    }
    
    public void acceptConnections(){
        try{
            System.out.println("aguardando conexões...");
            while(numPlayers < 2){
                Socket s = ss.accept();
                numPlayers++;
                System.out.println("Jogador #" + numPlayers + "se conectou");
                ServerSideConnection ssc  = new ServerSideConnection(s, numPlayers);
                if(numPlayers == 1){
                    player1 = ssc;
                }else{
                    player2 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("Agora temos 2 jogadores. Não serão aceitas mais conexões.");
        }catch (IOException ex){
            System.out.println("IOException from acceptConnections()");
            
        }
    }
    private class ServerSideConnection implements Runnable{
        
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;
        
        public ServerSideConnection(Socket s, int id){
            socket = s;
            playerID =  id;
            try{
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex){
                System.out.println("IOException from SSC constructor");
            }
        }
        
        @Override
        public void run(){
            try{
                dataOut.writeInt(playerID);
                dataOut.writeInt(numbermagic);
                dataOut.flush();
                
                while (true){
                    if(playerID == 1){
                        player1ButtonNum = dataIn.readInt();
                        player2.sendButtonNum(player1ButtonNum);
                    }else{
                        player2ButtonNum = dataIn.readInt();
                        player1.sendButtonNum(player2ButtonNum);
                    }
                    
                }
            }catch(IOException ex){
                System.out.println("IOException from run() SSC");
            }  
        }
        
        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            }catch(IOException ex){
                System.out.println("IOExcepition from sendButtonNum() SSC");
            }
        }
    }
    
    public static void main(String[] args){
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
    
}
