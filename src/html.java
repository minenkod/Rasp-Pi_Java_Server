import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class html {

     String searched = "";

    public  html(String search)
    {
        search = search.replaceAll("\\s+","+");
        searched = search;
    }

    String link = "https://www.youtube.com/results?search_query=";

    public  String getURLSource() throws IOException
    {
try {


        URL urlObject = new URL(link +  searched);
        URLConnection urlConnection = urlObject.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        String result = toString(urlConnection.getInputStream());
   return  result;
}catch (Exception e){
    System.out.println(e.getCause());

}
return "";
    }

    private static String toString(InputStream inputStream) throws IOException
    {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8")))
        {
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }

            return stringBuilder.toString();
        }
    }
}
