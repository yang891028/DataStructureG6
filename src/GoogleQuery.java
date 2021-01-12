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
import java.util.*;


public class GoogleQuery 

{

	public String searchKeyword;

	public String url;

	public String content;
	

	public GoogleQuery(String searchKeyword)

	{

		this.searchKeyword = searchKeyword;

		this.url = "http://www.google.com/search?q="+searchKeyword+"&oe=utf8&num=10";

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
	public ArrayList<WebPage> query() throws IOException

	{

		if(content==null)

		{

			content= fetchContent();

		}
		ArrayList<WebPage> pageList=new ArrayList<WebPage>();
		ArrayList<Double> scoreList=new ArrayList<Double>();
		Document doc = Jsoup.parse(content);
//		System.out.println(doc.text());
		Elements lis = doc.select("div");
		lis = lis.select(".kCrYT");
		for(Element li : lis)
		{
			try 
			{
				String citeUrl = li.select("a").get(0).attr("href");
				String title = li.select("a").get(0).select(".vvjwJb").text();
//				System.out.println(title + ",CiteUrl:"+citeUrl.substring(7,citeUrl.length()));
				WebPage webPage=new WebPage("http://www.google.com.tw"+citeUrl,title);
				if(!title.equals(""))
				{
					int x=0;
					for(x=0;x<citeUrl.length()-1;x++)
					{
						if(citeUrl.substring(x,x+3).equals("&sa"))
						{
							break;	
						}
					}
					citeUrl=citeUrl.substring(7,x);
					double score=countScore(citeUrl,title);
					pageList.add(webPage);
					scoreList.add(score);
				}
			}
			catch (IndexOutOfBoundsException e) 
			{
//				e.printStackTrace();
			}

			

		}
//		System.out.println("ScoreList Sizeeeeee:"+scoreList.size());
		ArrayList<WebPage> sortedList=sort(pageList,scoreList);
//		System.out.println("AAAAAAAAAA"+sortedList.size());
		return sortedList;

	}
	public double countScore(String citeUrl, String title) throws IOException
	{
		double score=0;
		ArrayList<Keyword> keywords = new ArrayList<Keyword>();
		Keyword k1=new Keyword("book", 20);
		Keyword k2=new Keyword("review",15);
		keywords.add(k1);
		keywords.add(k2);
		WebPage webPage=new WebPage(citeUrl,title);
		score=webPage.setScore(keywords);
		FindUrl subUrl = new FindUrl(citeUrl);
	    ArrayList<String> hrefList = subUrl.parser();
	    for (int i = 0; i < hrefList.size(); i++)
	    { 
	      	WebPage subPage=new WebPage(hrefList.get(i));
	       	score+=subPage.setScore(keywords);
//	       	FindUrl subSubUrl = new FindUrl(hrefList.get(i));
//		    ArrayList<String> subHrefList = subSubUrl.parser();
//		    for (int j = 0; j < subHrefList.size(); j++)
//		    { 
//		      	WebPage subSubPage=new WebPage(subHrefList.get(i));
//		       	score+=subSubPage.setScore(keywords);
//		       	System.out.println("SSUUUBBBB"+subHrefList.get(i)+"\n"+score);
//		    }
//	       	System.out.println(hrefList.get(i)+"\n"+score);
	    }
	    System.out.println("Total"+score);
        return score;
	}
	public ArrayList<WebPage> sort(ArrayList<WebPage> pageList,ArrayList<Double> scoreList)
	{
		ArrayList<WebPage> pageList1 = new ArrayList<WebPage>();
		ArrayList<Double> scoreList1 = new ArrayList<Double>();
		pageList1.add(pageList.get(0));
		scoreList1.add(scoreList.get(0));
		for(int i=1;i<scoreList.size();i++)
		{
			for(int j=0;j<scoreList1.size();j++)
			{
				if(scoreList.get(i)>=scoreList1.get(j))
				{
					pageList1.add(j, pageList.get(i));
					scoreList1.add(j, scoreList.get(i));
					break;
				}
				else if(j==scoreList1.size()-1)
				{
					scoreList1.add(scoreList.get(i));
					pageList1.add(pageList.get(i));
					break;
				}
			}
		}
		return pageList1;
	}
	

	

}