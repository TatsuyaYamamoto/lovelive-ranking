package net.sokontokoro_factory.api.game.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDto {
	@XmlElement
	private Integer id;
	
	@XmlElement
	private String name;
	
	@XmlElement
	private Boolean isAdmin;
	
	@XmlElement
	private Timestamp createDate;
	
	@XmlElement
	private Timestamp updateDate;
	
	@XmlElement
	private String iconURL;
	
	@XmlElement
	private List<ScoreDto> scores;
	
	
	/* ↓ setter and getter ↓ */
	
	public int getId() {
		return id.intValue();
	}
	public void setId(int id) {
		this.id = Integer.valueOf(id);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public Timestamp getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
	public List<ScoreDto> getScores() {
		return scores;
	}
	public void setScores(List<ScoreDto> scores) {
		this.scores = scores;
	}
}