package com.buaa.song.utils;

import cn.hutool.core.bean.BeanUtil;
import com.buaa.song.result.Result;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

/**
 * @FileName: RestUtil
 * @author: ProgrammerZhao
 * @Date: 2021/3/10
 * @Description:
 */
public class RestUtil {

    public static <T> Result get(RestTemplate template, String url, Class<T> dataType) {
        HttpHeaders header = new HttpHeaders();
        header.add("Request-From", "oj-service");
        HttpEntity httpEntity = new HttpEntity<>(header);
        Result result = template.exchange(url, HttpMethod.GET, httpEntity, Result.class).getBody();
        Object data = result.getData();
        if (data instanceof Map && data != null) {
            try {
                T instance = dataType.newInstance();
                BeanUtil.fillBeanWithMap((Map) data, instance, false);
                result.setData(instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T> Result get(RestTemplate template, String url, Map<String, Object> params, Class<T> dataType) {
        StringBuffer sb = new StringBuffer(url + "?");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String paramStr = key + "=" + params.get(key) + "&";
            sb.append(paramStr);
        }
        sb.deleteCharAt(sb.length() - 1);
        url = sb.toString();
        System.out.println(url);

        HttpHeaders header = new HttpHeaders();
        header.add("Request-From", "oj-service");
        HttpEntity httpEntity = new HttpEntity<>(header);

        Result result = template.exchange(url, HttpMethod.GET, httpEntity, Result.class)
                .getBody();

//        Result result = template.getForObject(url, Result.class, params);

        Object data = result.getData();
        if (data instanceof Map && data != null) {
            try {
                T instance = dataType.newInstance();
                BeanUtil.fillBeanWithMap((Map) data, instance, false);
                result.setData(instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T> Result post(RestTemplate template, String url, Class<T> dataType, @Nullable Object request) {
        HttpHeaders header = new HttpHeaders();
        header.add("Request-From", "oj-service");
        HttpEntity httpEntity = new HttpEntity<>(header);

        Result result = template.exchange(url, HttpMethod.POST, httpEntity, Result.class).getBody();
        Object data = result.getData();
        if (data instanceof Map && data != null) {
            try {
                T instance = dataType.newInstance();
                BeanUtil.fillBeanWithMap((Map) data, instance, false);
                result.setData(instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static <T> Result post(RestTemplate template, String url, Map<String, Object> params, Class<T> dataType,
                                  @Nullable Object request) {
        StringBuffer sb = new StringBuffer(url + "?");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String paramStr = key + "=" + params.get(key) + "&";
            sb.append(paramStr);
        }
        sb.deleteCharAt(sb.length() - 1);
        url = sb.toString();

        HttpHeaders header = new HttpHeaders();
        header.add("Request-From", "oj-service");
        HttpEntity httpEntity = new HttpEntity<>(header);

        Result result = template.exchange(url, HttpMethod.POST, httpEntity, Result.class).getBody();
        Object data = result.getData();
        if (data instanceof Map && data != null) {
            try {
                T instance = dataType.newInstance();
                BeanUtil.fillBeanWithMap((Map) data, instance, false);
                result.setData(instance);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
