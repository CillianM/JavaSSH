package com.company;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

//Compile command: javac -classpath .;jsch-0.1.53.jar JavaCMD.java
//Run Command: java -classpath .;jsch-0.1.53.jar JavaCMD

public class JavaCMD {

    public static void main(String[] args) {

        ArrayList<String> list = new ArrayList<>();
        CommandRunner runner = new CommandRunner();
        System.out.println("Enter a command");
        Scanner scan = new Scanner(System.in);
        String command = scan.nextLine();
        runner.runCommand(command);
    }

    private static class CommandRunner
    {
        private String host,user,password;

        //constructor to allow it to be filled in manually
        CommandRunner(String host,String user,String password)
        {
            this.host = host;
            this.user = user;
            this.password = password;
        }

        //Default constructor to ask user for detauls
        CommandRunner()
        {
            Scanner scan = new Scanner(System.in);
            System.out.println("Enter a host name");
            host = scan.next();
            System.out.println("Enter a user name");
            user = scan.next();
            System.out.println("Enter a password");
            password = scan.next();
        }

        //Runs a command and displays it's output
        void runCommand(String command)
        {
            ArrayList<String> outputList = new ArrayList<>();;

            try{
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                Session session=jsch.getSession(user, host, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.connect();
                System.out.println("Connected");

                Channel channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream in=channel.getInputStream();
                channel.connect();
                byte[] tmp=new byte[1024];
                while(true){
                    while(in.available()>0){
                        int i=in.read(tmp, 0, 1024);
                        if(i<0)break;
                        System.out.print(new String(tmp, 0, i));
                    }
                    if(channel.isClosed()){
                        System.out.println("exit-status: "+channel.getExitStatus());
                        break;
                    }
                    try{Thread.sleep(1000);}catch(Exception ee){}
                }
                channel.disconnect();
                session.disconnect();
                System.out.println("DONE");
            }catch(Exception e){
                e.printStackTrace();
            }
        }


        //Runs a command and returns an arraylist of the output for parsing
        //TODO Make this better if you want i don't care
        ArrayList<String> runCommand(String command,ArrayList<String> outputList)
        {
            try{
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jsch = new JSch();
                Session session=jsch.getSession(user, host, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.connect();
                System.out.println("Connected");

                Channel channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(command);
                channel.setInputStream(null);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream in=channel.getInputStream();
                channel.connect();
                byte[] tmp=new byte[1024];
                while(true){
                    while(in.available()>0){
                        int i=in.read(tmp, 0, 1024);
                        if(i<0)break;
                        outputList.add(new String(tmp, 0, i));
                    }
                    if(channel.isClosed()){
                        System.out.println("exit-status: "+channel.getExitStatus());
                        break;
                    }
                    try{Thread.sleep(1000);}catch(Exception ee){}
                }
                channel.disconnect();
                session.disconnect();
                System.out.println("DONE");
            }catch(Exception e){
                e.printStackTrace();
            }

            return outputList;
        }
    }
}
