/*
 ************************************
 * @项目名称: bb
 * @文件名称: GrpcClientProperties
 * @Date 2018/11/09
 * @Author will.zhao@bbex.io
 * @Copyright（C）: 2018 BitBili Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 **************************************
 */
package io.bbex.bb.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "bb.grpc-client")
public class GrpcClientProperties {

    private List<GrpcClient> list = new ArrayList<>();

}