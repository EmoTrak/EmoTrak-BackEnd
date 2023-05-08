package com.example.emotrak.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "months")
public class Month {

    @Id
    private int dailyMonth;

}