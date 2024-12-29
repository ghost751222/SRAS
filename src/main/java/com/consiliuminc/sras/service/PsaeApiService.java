package com.consiliuminc.sras.service;


import com.consiliuminc.sras.util.HttpClientUtils;
import com.consiliuminc.sras.util.JacksonUtils;
import com.consiliuminc.sras.vo.PsaeVo;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class PsaeApiService {
    PsaeVo psaeVo;

    private String psaeBaseRestUrl;
    private String userToken;

    private String getUserToken() {
        return userToken;
    }

    public PsaeApiService(PsaeVo vo) throws IOException {
        this.psaeVo = vo;
        this.psaeBaseRestUrl = String.format("http://%s:%d/PSAE/rest/v1", psaeVo.getIp(), psaeVo.getPort());
    }


    public boolean login(String username, String password) throws IOException {
        Map<String, Object> map = null;
        InputStream is = null;
        boolean IsLogin = false;
        try {
            HttpClient client = HttpClientUtils.getHttpClient();

            String url = new StringBuilder()
                    .append(this.psaeBaseRestUrl)
                    .append(new MessageFormat("/user/{0}/login").format(new Object[]{username}))
                    .toString();

            HttpPost post = HttpClientUtils.getHttpPostMethod(url);
            HttpClientUtils.addHeaderContentJson(post);
            Map<String, String> params = new HashMap<String, String>();
            params.put("passWord", password);
            StringEntity entity = new StringEntity(JacksonUtils.objectToJsonStr(params));
            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            is = response.getEntity().getContent();
            String content = IOUtils.toString(is, "UTF-8");

            map = JacksonUtils.jsonStrToMap(content);
            this.userToken = map.get("userToken").toString();
            IsLogin = true;
        } catch (Exception e) {
            throw e;
        } finally {
            IOUtils.closeQuietly(is);
        }
        return IsLogin;

    }

}
