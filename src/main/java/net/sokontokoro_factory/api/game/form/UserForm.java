package net.sokontokoro_factory.api.game.form;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserForm {
	
	@XmlElement(name = "user_id")
	private int userId;
	
	@XmlElement(name = "user_name")
	@Size(min = 1, max = 15)
	private String userName;
	
	
	/* ↓ setter and getter ↓ */
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}