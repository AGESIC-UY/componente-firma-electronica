package uy.gub.agesic.firma.cliente.ws.trust.client;


import java.util.Set;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.*;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

// Initialize OtputStream (fos) etc. ...

public boolean handleMessage (SOAPMessageContext c) {
   SOAPMessage msg = c.getMessage();

   boolean request = ((Boolean) c.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY)).booleanValue();

   try {
      if (request) { // This is a request message.
         // Write the message to the output stream 
         msg.writeTo (System.out);
      }
      else { // This is the response message 
         msg.writeTo (System.out);
      }
   }
   catch (Exception e) { }
   return true;
}

public boolean handleFault (SOAPMessageContext c) {
   SOAPMessage msg = c.getMessage();
   try {
      msg.writeTo (System.out);
   }
   catch (Exception e) {}
   return true;
}



public Set getHeaders() {
   // Not required for logging
   return null;
}

    @Override
    public void close(MessageContext context) {

    }
}
