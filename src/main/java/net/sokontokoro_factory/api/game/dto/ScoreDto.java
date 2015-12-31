package net.sokontokoro_factory.api.game.dto;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ScoreDto {
	
	@XmlElement(name = "game_name")
	private String gameName;
	
	@XmlElement(name = "user_id")
	private Integer userId;

	@XmlElement(name = "user_name")
	private String userName;
	
	@XmlElement(name = "point")
	private Integer point;
	
	@XmlElement(name = "create_date")
	private Timestamp createDate;
	
	@XmlElement(name = "update_date")
	private Timestamp updateDate;

	@XmlElement(name = "final_date")
	private Timestamp finalDate;

	@XmlElement(name = "count")
	private Integer count;
	
	@XmlElement(name = "ranking")
	private Integer ranking;

	/* ↓ setter and getter ↓ */
	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}

	public Timestamp getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Timestamp finalDate) {
		this.finalDate = finalDate;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}
}