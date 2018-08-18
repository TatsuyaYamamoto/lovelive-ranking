package net.sokontokoro_factory.lovelive.controller.form;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement
public class UpdateUserForm {
  @XmlElement(name = "user_name")
  @Getter
  @Setter
  private String userName;

  @XmlElement(name = "favorite")
  @Getter
  @Setter
  private String favorite;
}
