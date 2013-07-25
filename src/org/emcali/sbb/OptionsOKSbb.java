package org.emcali.sbb;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sip.ObjectInUseException;
import javax.sip.RequestEvent;
import javax.sip.ServerTransaction;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Response;
import javax.slee.ActivityContextInterface;
import javax.slee.RolledBackContext;
import javax.slee.SbbContext;

import net.java.slee.resource.sip.SleeSipProvider;

public abstract class OptionsOKSbb implements javax.slee.Sbb {
	
	private SleeSipProvider sipFactoryProvider;
	private MessageFactory messageFactory;

	public void onOPTIONS(RequestEvent event, ActivityContextInterface aci) {
		UserAgentHeader userAgentHeader = (UserAgentHeader) event.getRequest().getHeader(UserAgentHeader.NAME);
		if(!(userAgentHeader.getProduct().next().toString().indexOf("friendly") >= 0)){
			System.out.println("Options Request Received");
			ServerTransaction st = event.getServerTransaction();
			Response response;
			try {
				response = messageFactory.createResponse(Response.OK, event.getRequest());
				st.sendResponse(response);
				System.out.println("OK Response Sended");
				aci.detach(this.sbbContext.getSbbLocalObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else{
			try {
				event.getServerTransaction().terminate();
				aci.detach(this.sbbContext.getSbbLocalObject());
			} catch (ObjectInUseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onSUBSCRIBE(RequestEvent event, ActivityContextInterface aci){
		UserAgentHeader userAgentHeader = (UserAgentHeader) event.getRequest().getHeader(UserAgentHeader.NAME);
		if(userAgentHeader != null){
			if(!(userAgentHeader.getProduct().next().toString().indexOf("friendly") >= 0)){
				System.out.println("Subscribe Request Received");
				ServerTransaction st = event.getServerTransaction();
				Response response;
				ExpiresHeader expires = (ExpiresHeader) event.getRequest().getHeader(ExpiresHeader.NAME);
				try {
					response = messageFactory.createResponse(Response.OK, event.getRequest());
					response.setHeader(expires);
					st.sendResponse(response);
					System.out.println("OK Response Sended");
					aci.detach(this.sbbContext.getSbbLocalObject());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else{
				try {
					event.getServerTransaction().terminate();
					aci.detach(this.sbbContext.getSbbLocalObject());
				} catch (ObjectInUseException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Unsubscribe Request Received");
			ServerTransaction st = event.getServerTransaction();
			Response response;
			ExpiresHeader expires = (ExpiresHeader) event.getRequest().getHeader(ExpiresHeader.NAME);
			try {
				response = messageFactory.createResponse(Response.OK, event.getRequest());
				response.setHeader(expires);
				st.sendResponse(response);
				System.out.println("OK Response Sended");
				aci.detach(this.sbbContext.getSbbLocalObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) { 
		this.sbbContext = context; 
		try {
			Context ctx = (Context) new InitialContext().lookup("java:comp/env");
			sipFactoryProvider = (SleeSipProvider) ctx.lookup("slee/resources/jainsip/1.2/provider");
			sipFactoryProvider.getAddressFactory();
			sipFactoryProvider.getHeaderFactory();
			messageFactory = sipFactoryProvider.getMessageFactory();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
    public void unsetSbbContext() { this.sbbContext = null; }
    
    // TODO: Implement the lifecycle methods if required
    public void sbbCreate() throws javax.slee.CreateException {}
    public void sbbPostCreate() throws javax.slee.CreateException {}
    public void sbbActivate() {}
    public void sbbPassivate() {}
    public void sbbRemove() {}
    public void sbbLoad() {}
    public void sbbStore() {}
    public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
    public void sbbRolledBack(RolledBackContext context) {}

	
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove this 
	 * method, the sbbContext variable and the variable assignment in setSbbContext().
	 *
	 * @return this SBB's SbbContext object
	 */
	
	protected SbbContext getSbbContext() {
		return sbbContext;
	}

	private SbbContext sbbContext; // This SBB's SbbContext

}
