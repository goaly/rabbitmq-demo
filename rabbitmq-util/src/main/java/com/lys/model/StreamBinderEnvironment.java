/**
 * Copyright (C), 2022-2032
 */
package com.lys.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StreamBinderEnvironment
 *
 * @author: lys
 * @date: 2022/12/30 15:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamBinderEnvironment implements Serializable {

    private static final long serialVersionUID = -2672412991068959135L;

    private String host;

    private String addresses;

    private Integer port;

    private String username;

    private String password;

    private String virtualHost ;

}
