import java.io.BufferedReader;

import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.net.URL;

import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;



import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;

import org.jsoup.nodes.Element;

import org.jsoup.select.Elements;



public class GoogleQuery 

{

	public String searchKeyword;

	public String url;

	public String content;
	
	private WebPage rootPage;
	
	private WebTree tree;

	public GoogleQuery(String searchKeyword)

	{

		this.searchKeyword = searchKeyword;

		this.url = "http://www.google.com/search?q="+searchKeyword+"?oe=utf-8?num=20";
		rootPage = new WebPage("https://www.google.com/", "Google");		
		tree = new WebTree(rootPage);
	}

	

	private String fetchContent() throws IOException

	{
		String retVal = "";

		URL u = new URL(url);

		URLConnection conn = u.openConnection();

		conn.setRequestProperty("User-agent", "Chrome/7.0.517.44");

		InputStream in = conn.getInputStream();

		InputStreamReader inReader = new InputStreamReader(in,"utf-8");

		BufferedReader bufReader = new BufferedReader(inReader);
		String line = null;

		while((line=bufReader.readLine())!=null)
		{
			retVal += line;

		}
		return retVal;
	}
	public HashMap<String, String> query() throws IOException

	{

		if(content==null)

		{

			content= fetchContent();

		}

		HashMap<String, String> retVal = new HashMap<String, String>();
		
		Document doc = Jsoup.parse(content);
		System.out.println(doc.text());
		Elements lis = doc.select("div");
		// System.out.println(lis);
		lis = lis.select(".kCrYT");
		// System.out.println(lis.size());
		
		
		for(Element li : lis)
		{
			try 

			{
				String citeUrl = li.select("a").get(0).attr("href")+"\n";
				String title = li.select("a").get(0).select(".vvjwJb").text();
//				System.out.println("Original"+citeUrl);
//				System.out.println(citeUrl.substring(7,citeUrl.length()-1));
//				retVal.put("Website:"+title, ":"+citeUrl);
				int i=0;
				for(i=0;i<citeUrl.length()-1;i++)
				{
					if(citeUrl.substring(i,i+1).equals("&"))
					{
						break;	
					}
				}
				citeUrl=citeUrl.substring(7,i);
				System.out.println(citeUrl);
				if(citeUrl.contains("wiki")&&citeUrl.contains("%25")||citeUrl.contains("map")||citeUrl.contains("youtube"))
				{
					
				}
				else
				{
					saveToNode(citeUrl,title);
					calculateScore();
					
				}
				

			} catch (IndexOutOfBoundsException e) {

//				e.printStackTrace();

			}


		}
		return retVal;
		

		

	}
	public void saveToNode(String url,String title)
	{
		System.out.println("Here");
		tree.root.addChild(new WebNode(new WebPage(url,title)));
	}
	public void calculateScore()
	{
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		Keyword k1=new Keyword("novel", 2);
		Keyword k2=new Keyword("book",1);
		keywords.add(k1);
		keywords.add(k2);
		try {
			tree.setPostOrderScore(keywords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tree.eularPrintTree();
	}
	

	

}