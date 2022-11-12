package org.example.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AttendanceDTO implements Serializable {
    private String date;


    public Integer getYear(){
        String[] split = date.split("-");
        return new Integer(split[0]);
    }


    public Integer getMonth(){
        String[] split = date.split("-");
        return new Integer(split[1]);
    }
}
