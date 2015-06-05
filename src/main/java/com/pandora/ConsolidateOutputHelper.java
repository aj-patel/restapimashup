package com.pandora;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Callable;
import org.mule.api.transport.PropertyScope;

public class ConsolidateOutputHelper implements Callable{
	
	@Override
	public Object onCall(MuleEventContext eventContext) throws Exception {
		MuleMessage message = eventContext.getMessage();
		
		String name = (String)message.getProperty("artistName", PropertyScope.INVOCATION);

		Map<String,Object> topTrackDetails = (Map<String,Object>)message.getProperty("topTrackInfo", PropertyScope.INVOCATION);
		Map<String,Object> artistProfile = (Map<String,Object>)message.getProperty("artistProfile", PropertyScope.INVOCATION);
		
		Map<String,Object> tracksDetail = (Map<String,Object>)topTrackDetails.get("tracks");
		Map<String,Object> trackDetail = (Map<String,Object>)tracksDetail.get("track");
		tracksDetail.remove("@attr");
		Map<String,Object> artistDetail = (Map<String,Object>)trackDetail.get("artist");
		topTrackDetails.remove("tracks");
		Map<String,Object> artist = (Map<String,Object>)artistProfile.get("artist");
		for(Iterator<Map.Entry<String, Object>> it = trackDetail.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, Object> entry = it.next();
		      if(!entry.getKey().trim().equals("duration") && !entry.getKey().trim().equalsIgnoreCase("name") && !entry.getKey().equals("playcount") && !entry.getKey().equals("listeners") && !entry.getKey().equals("artist")) {
		        it.remove();
		      }
		    }
		for(Iterator<Map.Entry<String, Object>> it = artistDetail.entrySet().iterator(); it.hasNext(); ) {
		      Map.Entry<String, Object> entry = it.next();
		      if(!entry.getKey().equals("name")) {
		        it.remove();
		      }
		    }
		artistDetail.put("Biography", ((Map)artist.get("bio")).get("summary"));
		List<Object> similar = new ArrayList<Object>();
		for(int i =0; i<((List<Object>)(((Map<String,Object>)artist.get("similar"))).get("artist")).size();i++){
			Map<String,Object> mp = (Map<String,Object>)((List<Object>)(((Map<String,Object>)artist.get("similar"))).get("artist")).get(i);
			similar.add(mp.get("name"));
	}
		artistDetail.put("similar", similar);
		artistDetail.put("MusicBrainzScore", message.getProperty("artistRank", PropertyScope.INVOCATION));
		topTrackDetails.put("track", trackDetail);
		return topTrackDetails;
	}

}
