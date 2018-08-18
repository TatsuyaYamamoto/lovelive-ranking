package net.sokontokoro_factory.lovelive.controller.form;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@XmlRootElement
public class InsertScoreForm {
  @XmlElement(name = "point")
  @Getter
  @Setter
  private Integer point;
}
