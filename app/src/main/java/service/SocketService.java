package service;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketService {
    public static void main(String[] args) throws Exception {        //服务端在20006端口监听客户端请求的TCP连接
        ServerSocket server = new ServerSocket(20006);
        Socket client = null;
        boolean f = true;
        while (f) {
            //等待客户端的连接，如果没有获取连接
            client = server.accept();
            System.out.println("与客户端连接成功！");
            //为每个客户端连接开启一个线程
            new Thread(new ServerThread(client)).start();
        }
        server.close();

    }


    public static class ServerThread implements Runnable {
        private Socket client = null;

        public ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {            //获取Socket的输出流，用来向客户端发送数据
                try {
                    PrintStream out = new PrintStream(client.getOutputStream());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
                    //写入
                    writer.write("{\"type\": \"command\", \"method\": \"initialize\", \"params\": [{\"string\": \"6c06d1b0d9015e47ec144097\"}], \"return\": \"bool\"}\r\n  {\"type\":\"command\",\"module\": \"client\",\"method\": \"login\",\"params\": [{\"string\": \"423424\"},{\"string\": \"234234354\"}],\"return\":\"bool\"}\r\n");
                    //发送
                    writer.flush();            //缓冲流刷新
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //获取Socket的输入流，用来接收从客户端发送过来的数据
                BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                boolean flag = true;
                System.out.println("startREAD");
                while (flag) {                //接收从客户端发送过来的数据
                    String str = buf.readLine();
                    System.out.println("str:" + str);
                    if (str == null || "".equals(str)) {
                        flag = false;
                    } else {
                        if ("bye".equals(str)) {
                            flag = false;
                        } else {                        //将接收到的字符串前面加上echo，发送到对应的客户端						out.println("echo:" + str);					}				}			}			out.close();			client.close();		}catch(Exception e){			e.printStackTrace();		}	}

                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}