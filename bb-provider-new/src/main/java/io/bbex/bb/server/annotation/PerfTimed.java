/**********************************
 * @项目名称: trade
 * @文件名称: PerfTimed
 * @Date 2018/7/14
 * @Author fulintang
 * @Copyright（C）: 2018 BitBili Inc. All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 ***************************************/


package io.bbex.bb.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface PerfTimed {
}
