package org.example;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Job;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MyApplication.class)
public class BaseTest {


    @Test
    public void test() throws Exception {
        JenkinsServer jenkins = new JenkinsServer(new URI("https://jks.jaalantech.com"), "xiezhao", "xiezhao");
        Map<String, Job> jobs = jenkins.getJobs();


        jobs.forEach((x, y) -> {
            if (x.equals("qj-erp-gateway")) {
                System.out.println(x);
                Map<String, String> map = new HashMap<>();
                map.put("deployEnv", "sit01");
                map.put("branch", "feature_sprint38");
                try {
                    y.build(map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
