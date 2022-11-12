package org.example;

import com.alibaba.fastjson2.JSONObject;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.sun.xml.internal.xsom.XSUnionSimpleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/callback")
public class MyController {

    /*

{
	"conversationId": "cidIW7WZT7eR+uiFTOEcCW82w==",
	"atUsers": [{
		"dingtalkId": "$:LWCP_v1:$u8N1RuYloc5Ijtrxw28e/Pau34l2zl8+"
	}],
	"chatbotCorpId": "ding4926b640107078f2ffe93478753d9884",
	"chatbotUserId": "$:LWCP_v1:$u8N1RuYloc5Ijtrxw28e/Pau34l2zl8+",
	"msgId": "msgm9oYEOyaIS5Ok6mUnIA0bA==",
	"senderNick": "谢朝",
	"isAdmin": false,
	"senderStaffId": "0151556648401138491",
	"sessionWebhookExpiredTime": 1665736776617,
	"createAt": 1665731376467,
	"senderCorpId": "ding4926b640107078f2ffe93478753d9884",
	"conversationType": "2",
	"senderId": "$:LWCP_v1:$7f5Ln9eXgzdjNFbjuG73YpvhQKoZmIlW",
	"conversationTitle": "告警测试",
	"isInAtList": true,
	"sessionWebhook": "https://oapi.dingtalk.com/robot/sendBySession?session=ba6b7c3a70f9563d177caaa5f251f864",
	"text": {
		"content": " hello world!"
	},
	"robotCode": "dingpjrwhlcru3xms5ar",
	"msgtype": "text"
}

     */
    @PostMapping("/robot/dingtalk")
    public void callbackDingTalk(@RequestBody String content){
        log.info(content);
        JSONObject jsonObject = JSONObject.parseObject(content);
        String senderNick = (String) jsonObject.get("senderNick");
        JSONObject text = jsonObject.getJSONObject("text");
        String contentReceive = (String)  text.get("content");
        if (contentReceive.contains("prod")) {
            if (!senderNick.equals("谢朝")) {
                return;
            } else {
                //直接构建不加参数
                try {
                    //https://jks.jaalantech.com
                    JenkinsServer jenkins = new JenkinsServer(new URI("https://jks.jaalantech.com"), "xiezhao", "xiezhao");
                    contentReceive = contentReceive.trim();
                    JobWithDetails job = jenkins.getJob(contentReceive);
                    job.build();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        String[] s = contentReceive.trim().split("\\s+");
        String project = s[0];
        String env = s[1];
        String branch = s[2];
        String frontBranch = s[3];

        HashMap<String, String> map = new HashMap<>();
        map.put("deployEnv", env);
        map.put("branch", branch);

        if (StringUtils.hasLength(frontBranch)) {
            map.put("project", frontBranch);
        }

        try {
            JenkinsServer jenkins = new JenkinsServer(new URI("https://jks.jaalantech.com"), "xiezhao", "xiezhao");
            JobWithDetails job = jenkins.getJob(project);
            job.build(map);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class T {
        private String name;

        public T(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static void main(String[] args) {
        List<T> a = new ArrayList();
        a.add(new T("a"));
        a.add(new T("b"));
        a.add(new T("c"));

        a.get(0).setName("a1");
        a.get(a.size() - 1).setName("c2");

        System.out.println(a);
    }
}
