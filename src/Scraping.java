import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Scraping{
    public static void main(String[] args) throws IOException, JSONException {
        
        //String url = "http://collections.vam.ac.uk/search/?listing_type=list&offset=0&limit=15&narrow=1&extrasearch=&q=&quality=2&objectnamesearch=&placesearch=&after=&after-adbc=AD&before=&before-adbc=AD&namesearch=&materialsearch=&mnsearch=&locationsearch=";
        
    	JSONArray jsonarr = new JSONArray();
    	
    	
    	String url = "http://collections.vam.ac.uk/search/?limit=15&narrow=1&commit=Search&quality=2&after-adbc=AD&before-adbc=AD&category%5B0%5D=44&technique%5B0%5D=30&offset=0&slug=0";
    	
    	int counter=1;
    	while(url!=""&&counter<400)
    	{
    	
    	
    		Document doc = Jsoup.connect(url).get();
        
    		Elements listView = doc.select("div#customise");
        
    		Elements lstView = listView.select("li.last > a[href]");
    		url=lstView.attr("abs:href");
        
    		doc = Jsoup.connect(url).get();
    		Elements links = doc.select("ul.object-row");
    		Elements nextLink = doc.select("a:contains(Next)");
        
        
              
    		//System.out.println("\nLinks:"+ links.size());
    		for (Element link : links) 
    		{
        	
        	
    			Elements childnodes= link.select("a[href].no-bg");
        	
    			Elements date = link.select("li.date");
    			String pattern = ".*[^1][^6][^0-9]{2}[1][7-9][0-9][0-9].*";
    			String pattern1 = ".*[2][0-9]{3}.*";
    			String pattern2 = ".*[1][8-9]th century.*";
    			String pattern3 = ".*[2][0-9]th century.*";
        
    			Elements place = link.select("li.place");
        	
        	
    			if((date.toString()).matches(pattern)||(date.toString()).matches(pattern1)||(date.toString()).matches(pattern2)||(date.toString()).matches(pattern3))
    			{
    				if((place.toString()).contains("England")||(place.toString()).contains("America")||(place.toString()).contains("France")||(place.toString()).contains("Paris")||(place.toString()).contains("Belgium")||(place.toString()).contains("Spain")||(place.toString()).contains("Italy")||(place.toString()).contains("Great Britain")||(place.toString()).contains("London")||(place.toString()).contains("Rome")||(place.toString()).contains("Oxford")||(place.toString()).contains("United States")||(place.toString()).contains("Germany")||(place.toString()).contains("United Kingdom")||(place.toString()).contains("USA"))
    				{
    					JSONObject jsonobj = new JSONObject();
        	    
    					System.out.println("Artwork number: "+counter); 
    					jsonobj.put("Artwork Number", counter);
        		
    					Document doc1 = Jsoup.connect(childnodes.attr("abs:href")).get();
    					Elements details = doc1.select("div#basic-details");
           
    					Elements list_details=details.select("li:has(strong)");
            
    					// Elements art_technique=list_details.select("strong:contains(Techniques:)+div.content");
    					//Elements art_category=list_details.select("strong:contains(Categories:)+div.content");
    					//String place1=place_of_origin.text().toString();            
                     
    					Elements imgurl = doc1.select("img#main_image");
            
    					System.out.println("Title: "+details.select("h1").text());
    					jsonobj.put("Title", details.select("h1").text().toString());
            
    					System.out.println("Image URL"+" : "+imgurl.attr("abs:src"));
    					jsonobj.put("Image URL", imgurl.attr("abs:src").toString());
            
           
    					Elements more_info = doc1.select("div#pane-more-information");
            
    					Elements contents = more_info.select("h2,h2+p");
    					int i=0;
    					List<String> label = new ArrayList<String>();
    					List<String> content = new ArrayList<String>();
            
    					for(Element heading : contents)
    					{
    						if(i%2==0)
    						{
    							label.add(heading.text());
    							System.out.print(heading.text()+" : ");
            		
    						}
    						else
    						{
    							content.add(heading.text());
        						System.out.println(heading.text());
    							            		
    						}
    						i++;
    					}
            
    					for(int c=0;c<label.size();c++)
    					{
    						jsonobj.put(label.get(c),content.get(c));
    					}
            
    					for(Element lst : list_details)
    					{
    						if(!lst.select("strong").text().equalsIgnoreCase("Object:")&&!lst.select("strong").text().equalsIgnoreCase("Place of origin:")&&!lst.select("strong").text().equalsIgnoreCase("Date:")&&!lst.select("strong").text().equalsIgnoreCase("Artist/Maker:")&&!lst.select("strong").text().equalsIgnoreCase("Materials and Techniques:"))
    						{  	
    							System.out.println(lst.select("strong").text()+"  "+lst.select("div.content").text());
    							jsonobj.put(lst.select("strong").text().replaceAll(":", ""),lst.select("div.content").text());
    						}
            	
    					}
            
    					Elements summary = doc1.select("div#pane-summary p");
            
    					System.out.println("Summary: "+summary.text().toString());
    					jsonobj.put("Summary",summary.text().toString());
    					counter++;
    					jsonarr.put(jsonobj);
           
    					System.out.println("---------------------------------------------------------------------");
            
    				}
    			}
        	
        	
    		}
        
    		url=nextLink.attr("abs:href");
    		url=url.trim();
    		//System.out.println("URL:"+url);
         
        
    	}
    	
    	System.out.println("Ended");
    	
    	FileWriter file = new FileWriter("C:/Users/Chandana/Documents/dataset.txt");
        try {
            file.write(jsonarr.toString());
            System.out.println("Successfully Copied JSON Object to File...");
            System.out.println("\nJSON Object: " + jsonarr);
 
        } catch (IOException e) {
            e.printStackTrace();
 
        } finally {
            file.flush();
            file.close();
        }
    	
    	
    }

   
}

