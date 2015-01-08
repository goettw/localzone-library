package info.localzone.communication.model;

public class Payload {
	public Payload() {

	}
	String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Payload(String subject, String body) {
		super();
		this.subject = subject;
		this.body = body;
	}
	String body;
}
