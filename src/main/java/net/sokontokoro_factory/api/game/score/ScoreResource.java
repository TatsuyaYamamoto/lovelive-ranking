package net.sokontokoro_factory.api.game.score;

import javax.xml.bind.annotation.XmlRootElement;


//{
//	game_name: "hoge",
//	category: "hogehoge",
//	user_name: "fugaduga",
//	point: 123
//}

@XmlRootElement
public class ScoreResource {
	public String game_name;
	public String category;
	public int user_id;
	public int point;
}






