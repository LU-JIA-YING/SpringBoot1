package com.example.Spring.controller.dto.response;

import com.example.Spring.model.entity.Mgni;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)   //  Xml排序
@XmlRootElement //  將Java類或枚舉類型映射到XML元素。（生成的xml根節點，默認是class名）
public class MgniResponse {

    List<Mgni> mgniList;
}
