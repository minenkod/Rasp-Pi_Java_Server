import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.cert.TrustAnchor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class a {
    public final static int SOCKET_PORT = 9696;  // 6969 laptop 9696 rasp 2
    //Text read
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static String message;
    //

    public static void main(String[] args) throws IOException {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        ServerSocket servsock = null;
        Socket sock = null;
        try {
            servsock = new ServerSocket(SOCKET_PORT);
            while (true) {
                System.out.println("Waiting...");
                try {
                    sock = servsock.accept();
                    System.out.println("Accepted connection : " + sock);
                    // send file
                    //text read
                    DataInputStream in = new DataInputStream(sock.getInputStream());
                    String response =in.readUTF();

                    if (response.contains("[.mp3]")) { //
                        //Determine name of the song and send it.
                        //Download the song using youtube-dl. Use result to set File_To_Send
                        String url = response.substring(response.indexOf("]") + 1);
                        //url = url.substring(0, url.indexOf('-'));
                        url = "https://www.youtube.com" + url;
                        System.out.println("URL is: " + url);
                        String FileSend = downSong(url);

                        System.out.println("FileSend: " + FileSend);
                        File myFile = new File("./" + FileSend);
                        byte[] mybytearray = new byte[(int) myFile.length()];
                        fis = new FileInputStream(myFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        os = sock.getOutputStream();
                        System.out.println("Sending " + FileSend + "(" + mybytearray.length + " bytes)");
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                    }
                    else if(response.contains("[down]"))
                    {
                         String FileSend = response.substring(response.indexOf("]") + 1);
                        System.out.println("FileSend: " + FileSend);
                        File myFile = new File("./" + FileSend);
                        byte[] mybytearray = new byte[(int) myFile.length()];
                        fis = new FileInputStream(myFile);
                        bis = new BufferedInputStream(fis);
                        bis.read(mybytearray, 0, mybytearray.length);
                        os = sock.getOutputStream();
                        System.out.println("Sending " + FileSend + "(" + mybytearray.length + " bytes)");
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                    }
                    else if(response.contains("[files]")){
                         String message = fileNames();
                        DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                        out.writeUTF(message);
                    }
                    else if (response.contains("[html]")) {
                        try {
                            String search = response.substring(response.indexOf("]") + 1);
                            String result = getHTML(search);
                            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
                            out.writeUTF(result);
                        } catch (Exception e) {
                            System.out.println("Exception:" + e.getCause());
                        }
                    }
                    System.out.println("Done.");
                }
                catch(Exception e){System.out.println("Error: " + e.getMessage());}
                finally {

                    if (bis != null) bis.close();
                    if (os != null) os.close();
                    if (sock != null) sock.close();

                }
            }
        } finally {
            if (servsock != null) servsock.close();

        }

    }

static String downSong(String url)
{
    String mp4 = "";

    String fileName = "";
    try {
        //youtube-dl -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 "https://www.youtube.com/watch?v=KCa12USVYWw"
        ArrayList al = new ArrayList();
String command = "youtube-dl -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 "  + url;
System.out.println(command);
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        StringBuilder sb = new StringBuilder();
String output= "";
        String line;
        BufferedReader error = new BufferedReader(new InputStreamReader(p.getInputStream()));
        while ((line = error.readLine()) != null) {
          //  System.out.println(line);
            sb.append(line + "\n");

        }
        String resultScr = sb.toString();
        Pattern pattern = Pattern.compile(" Destination: .*.mp3");   //Pattern for the titles

        Matcher matcher = pattern.matcher(resultScr);
        System.out.println(output);
        while (matcher.find()) {
            String x = matcher.group();
        //    System.out.println("******** " + x);
            x = x.substring(x.indexOf(':') + 1);
        x= x.substring(x.indexOf(" ") + 1);
            al.add(x);
        }

        for (String sd : new ArrayList<String>(al))
        {
            System.out.println( sd);
            fileName = sd;
        }
    }catch (Exception e) {}

return  fileName;
}

    static String getHTML(String search) {
        StringBuilder sb = new StringBuilder();
        try {
            String searchForm = search.replaceAll("\\s","+");
             Process p = Runtime.getRuntime().exec("mono GetHTML.exe " + searchForm);
            //process.redirectErrorStream(true);
        p.waitFor();
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            // System.out.println("Output of running ");
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Exception:" + e.getCause());
        }
        return sb.toString();
    }
static String fileNames()
{
   // refreshDB();
    StringBuilder sb = new StringBuilder();

    File folder = new File("./");
    File[] listOfFiles = folder.listFiles();
    int id = 0;
    String filename;
    for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
            filename = listOfFiles[i].getName();
            sb.append(filename + "\n");
            id++;
        }
    }
    return  sb.toString();
}
static   void refreshDB()
{
    InsertApp app = new InsertApp();
    app.clearDB();
    File folder = new File("./");
    File[] listOfFiles = folder.listFiles();
    int id = 0;
    String filename;
    for (int i = 0; i < listOfFiles.length; i++) {
        if (listOfFiles[i].isFile()) {
            filename = listOfFiles[i].getName();
            app.insert(id, filename);
            id++;
        }
    }
}


}