package gameClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class KML_Logger {

	private StringBuilder string;
    private int scenario_num;
    

    public  KML_Logger(){
    	;
    }
   
    
    public KML_Logger(int scenario_num) {
        this.scenario_num = scenario_num;
        string = new StringBuilder();
        start();
    }

    
    public void start()
    {
    	string.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" );
    			string.append(               "<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" );
    					string.append(     "  <Document>\r\n" );
    					string.append("  <name>" + "Game stage :"+this.scenario_num + "</name>" +"\r\n");
    					string.append(" <Style id=\"node\">\r\n");
    					string.append(   "      <IconStyle>\r\n" );
    					string.append(    "        <Icon>\r\n" );
    					string.append(    "          <href>http://maps.google.com/mapfiles/kml/pal3/icon35.png</href>\r\n");
    					string.append(    "        </Icon>\r\n" );
    					string.append( "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n");
    					string.append(  "      </IconStyle>\r\n");
    					string.append( "    </Style>" );
    					string.append(     " <Style id=\"banana\">\r\n" );
    					string.append(    "      <IconStyle>\r\n" );
    					string.append(    "        <Icon>\r\n" );
    					string.append(    "          <href>http://maps.google.com/mapfiles/kml/pal5/icon49.png</href>\r\n" );
    					string.append("        </Icon>\r\n" );
    					string.append( "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" );
    					string.append(  "      </IconStyle>\r\n" );
    					string.append(  "    </Style>" );
    					string.append(" <Style id=\"apple\">\r\n" );
    					string.append( "      <IconStyle>\r\n" );
    					string.append( "        <Icon>\r\n" );
    					string.append( "          <href>http://maps.google.com/mapfiles/kml/pal5/icon56.png</href>\r\n" );
    					string.append( "        </Icon>\r\n" );
    					string.append(  "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" );
    					string.append("      </IconStyle>\r\n" );
    					string.append( "    </Style>" );
                        string.append(" <Style id=\"robot\">\r\n" );
                        string.append( "      <IconStyle>\r\n" );
                        string.append("        <Icon>\r\n" );
                        string.append( "          <href>http://maps.google.com/mapfiles/kml/pal4/icon26.png></href>\r\n" );
                        string.append( "        </Icon>\r\n" );
                        string.append("        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" );
                        string.append( "      </IconStyle>\r\n");
                        string.append(  "    </Style>");
    }

   
    
    public void addPlaceMark(String id, String position)
    {
        LocalDateTime time = LocalDateTime.now();
        		string.append( "    <Placemark>\r\n" );
        		string.append(    "      <TimeStamp>\r\n" );
        		string.append(      "        <when>" + time+ "</when>\r\n" );
        		string.append(     "      </TimeStamp>\r\n" );
        		string.append(       "      <styleUrl>#" + id + "</styleUrl>\r\n" );
        		string.append(        "      <Point>\r\n" );
        		string.append(        "        <coordinates>" + position + "</coordinates>\r\n" );
        		string.append(        "      </Point>\r\n" );
        		string.append(        "    </Placemark>\r\n");



    }

 
    public void end() {
		string.append("  </Document>\r\n");
		string.append("</kml>");
		try {
			PrintWriter kml = new PrintWriter(new File("data/" + scenario_num + ".kml"));
			kml.write(string.toString());
			kml.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
	}
    

    }
