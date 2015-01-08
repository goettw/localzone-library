package info.localzone.communication.model;

public class Message {
	public Message() {
		
	}
	Payload payload;
	Header header;
	public Message(Header header, Payload payload) {
		super();
		this.payload = payload;
		this.header = header;
	}
;
	public Payload getPayload() {
		return payload;
	}
	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	public Header getHeader() {
		return header;
	}
	public void setHeader(Header header) {
		this.header = header;
	}
	public String toString () {
		StringBuffer sb = new StringBuffer ();
		if (header == null)
			sb.append("header:null");
		else
			sb.append(header);
		sb.append ("\n");
		if (payload == null)
			sb.append("header:null");
		else
			sb.append(payload);
		return sb.toString();
	}
}
