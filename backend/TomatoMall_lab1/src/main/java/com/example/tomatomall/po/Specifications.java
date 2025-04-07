<<<<<<< HEAD
package com.example.tomatomall.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Specifications {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "specifications_id")
    private Integer id;

    @Basic
    @Column(name = "item")
    private String item;

    @Basic
    @Column(name = "value")
    private String value;
}
=======
package com.example.tomatomall.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Specifications {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "specifications_id")
    private Integer id;

    @Basic
    @Column(name = "item")
    private String item;

    @Basic
    @Column(name = "value")
    private String value;
}
>>>>>>> 431dbecd26ca9ceb77461c91897a01de963014ae
