package net.sokontokoro_factory.api.game.form;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScoreForm {

	@XmlElement(name = "game_name")
	private String gameName;
	
	@XmlElement(name = "user_id")
	private int userId;

	@XmlElement(name = "point")
	private int point;

	@XmlElement(name = "skntkt_token")
	private String skntktToken;

	/* ↓ setter and getter ↓ */
	public String getGameName() {
		return gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}
	public int getPoint() {
		return point;
	}
	public void setPoint(int point) {
		this.point = point;
	}
	public String getSkntktToken() {
		return skntktToken;
	}
	public void setSkntktToken(String skntktToken) {
		this.skntktToken = skntktToken;
	}
}