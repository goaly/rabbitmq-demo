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
 * StreamBinder
 *
 * @author: lys
 * @date: 2022/12/29 17:12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamBinder implements Serializable {

    private static final long serialVersionUID = 222065539518885421L;

    private String name;

    private String type;

    private StreamBinderEnvironment environment;

}
