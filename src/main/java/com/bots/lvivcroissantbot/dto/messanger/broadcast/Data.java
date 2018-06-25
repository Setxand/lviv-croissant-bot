package com.bots.lvivcroissantbot.dto.messanger.broadcast;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class Data {
    private String name;
    private String period;
    private List<Value>values;
    private String title;
    private String description;
    private String id;
    private Paging paging;

    @Override
    public String toString() {
        return "\n\nData{" +
                "name='" + name + '\'' +
                ",\n period='" + period + '\'' +
                ",\n values=" + values +
                ",\n title='" + title + '\'' +
                ",\n description='" + description + '\'' +
                ",\n id='" + id + '\'' +
                ",\n paging=" + paging +
                '}';
    }
}
