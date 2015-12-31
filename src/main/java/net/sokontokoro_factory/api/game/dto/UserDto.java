package net.sokontokoro_factory.api.game.dto;

import java.sql.Timestamp;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDto {
	@XmlElement(name = "user_id")
	private Integer id;
	
	@XmlElement(name = "user_name")
	private String name;
	
	@XmlElement(name = "admin")
	private Boolean admin;
	
	@XmlElement(name = "deleted")
	private Boolean deleted;
	
	@XmlElement(name = "create_date")
	private Timestamp createDate;
	
	@XmlElement(name = "update_date")
	private Timestamp updateDate;
	
	@XmlElement(name = "icon_url")
	private String iconURL;
	
	@XmlElement(name = "scores")
	private List<ScoreDto> scores;
	
	/* ↓ コンストラクタ ↓ */
	public UserDto(){}
	
	public UserDto(int id){
		this.id = Integer.valueOf(id);
	}
	
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
	public Boolean getAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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