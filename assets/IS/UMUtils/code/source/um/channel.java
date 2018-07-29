package um;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2015-06-01 11:52:51 EDT
// -----( ON-HOST: 192.168.26.40

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import com.pcbsys.nirvana.client.*;
// --- <<IS-END-IMPORTS>> ---

public final class channel

{
	// ---( internal utility methods )---

	final static channel _instance = new channel();

	static channel _newInstance() { return new channel(); }

	static channel _cast(Object o) { return (channel)o; }

	// ---( server methods )---




	public static final void getMessageCount (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(getMessageCount)>> ---
		// @sigtype java 3.5
		// [i] field:0:required name
		// [i] field:0:required realm
		// [i] field:0:required type {"channel","queue"}
		// [o] field:0:required queueLength
		// [o] field:0:optional errorMessage
		IDataCursor cursor = pipeline.getCursor();
		
		String channelName = null;
		String realmServer = null;
		String mode = null;
		
		if (cursor.first("name"))
		{
			channelName = (String) cursor.getValue();			
		}
		else {
			return;
		}
		
		if (cursor.first("realm"))
		{
			realmServer = (String) cursor.getValue();
		}
		else {
			return;
		}
		
		if (cursor.first("type"))
		{
			mode = (String) cursor.getValue();
		}
		else {
			return;
		}
		
		try {
			
			long count = 0;
			long first = 0;
			long last = 0;	
			String queueLength = null;
			String sample = "test";
			//Create logical connection to Universal Messaging
			//Create nSession Object
			String[] RNAME={realmServer}; 
			nSessionAttributes nsa=new nSessionAttributes(RNAME); 
			nSession mySession=nSessionFactory.create(nsa); 
			mySession.init();
			
			//Create channel attribute. Set channel name
			nChannelAttributes cattrib = new nChannelAttributes(); 
			cattrib.setName(channelName); 
			
			if (mode.equalsIgnoreCase("channel")){
			
			//Obtain channel reference
			nChannel myChannel=mySession.findChannel(cattrib);
			int channelType = myChannel.getType();
			
			if (channelType == 5) {
				
				// throw new ServiceException ("Cannot calculate depth for Transient Channel Type");
			} else {
				cursor.insertAfter("channelType", channelType);
			}
			nChannelIterator iterator = myChannel.createIterator();
			nConsumeEvent event1 = iterator.getFirst();			
			if (event1 != null){
				first = event1.getEventID();				
				nConsumeEvent event2 = iterator.getLast();
				if (event2 != null){
					last = event2.getEventID();
					if (first == last){
					} else {
					last++;			
					}
				}
			} else {
				cursor.insertAfter("sample", sample);
			}	
			if (first == last && first != 0)
			{
				count++;
			} else {
				count = last - first;
			}
			
			} else {
			
			//Obtain queue reference
			nQueue myQueue = mySession.findQueue(cattrib);
			nQueueDetails details = myQueue.getDetails();			
			count = details.getNoOfEvents();
			}
			
			//Get queue length for channel and queue
			queueLength = String.valueOf(count);
			cursor.insertAfter("queueLength", queueLength);
			
			nSessionFactory.close(mySession);
			cursor.destroy();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			String errorMessage="error message: "+ e +"";
			IDataUtil.put( cursor, "errorMessage", errorMessage );
			
		} 
			
		// --- <<IS-END>> ---

                
	}
}

