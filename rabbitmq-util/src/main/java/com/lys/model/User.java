/**
 * Copyright (C), 2022-2032
 */
package com.lys.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * User 用户模型
 * @author: lys
 * @date: 2022/5/15 16:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 2908981317996463146L;

    private String id;

    private String userName;

    private String gender;

    private String title;

    private String phone;
}
