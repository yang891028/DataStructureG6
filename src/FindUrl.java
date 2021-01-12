import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FindUrl {
    String htmlUrl;
    ArrayList<String> hrefList = new ArrayList();
    String charSet;

    public FindUrl(String htmlUrl) {
        // TODO �۰ʥͦ����غc�禡�s��
        this.htmlUrl = htmlUrl;
    }

    public ArrayList<String> parser() throws IOException {  //��o�Ӻ����U���W�s��
        try {
        	 URL url = new URL(htmlUrl);           //�إ�URL����A�إ߳s�u
             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             connection.setDoOutput(true);
             String contenttype = connection.getContentType();
             charSet = getCharset(contenttype);
             InputStreamReader isr = new InputStreamReader(connection.getInputStream(), "utf-8");   //�إ߿�J�y
             BufferedReader br = new BufferedReader(isr);
             String str = null, rs = null;
             int i=0;
             while ((str = br.readLine()) != null&&i<10) {
                 Pattern pattern = Pattern.compile("<a href=(.*?)>");    //�ѧO�o�@��O�_�ŦX�������榡
                 Matcher matcher = pattern.matcher(str);

                 while (matcher.find()) {
                     Pattern pattern1 = Pattern.compile("\"(.*?)\"");
                     Matcher matcher1 = pattern1.matcher(matcher.group(1));
                     if (matcher1.find()) {
                         rs = matcher1.group(1);      //�N����޸��������e�I���X��
                     }
                     if (rs.indexOf("http") != -1) {  //�ahttp����URL
                         if (rs != null)
                         {
                             hrefList.add(rs);
                             i++;
                         }
                     }
                 }
             }
             return hrefList;
        }
        catch(Exception e)
        {
        	hrefList.add("");
        }
        return hrefList;
    }
    private String getCharset(String str) {  //��������s�X�覡�A���Ǻ����S�����ѡA�ҥH�Ȯɤ��ϥ�
    	if(str!=null)
    	{
	        Pattern pattern = Pattern.compile("charset=.*");
	        Matcher matcher = pattern.matcher(str);
	        if (matcher.find())
	            return matcher.group(0).split("charset=")[1];
    	}
        return null;
    }
}