package demo;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

import static java.util.Collections.singletonMap;

@EnableAutoConfiguration
@ComponentScan
@Controller
@Configuration
@EnableEurekaClient
@EnableHystrix
public class App {
    @Autowired
    RemoteCommand remoteCommand;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @ModelAttribute
    ShortenForm setupForm() {
        return new ShortenForm();
    }

    @RequestMapping("/")
    String home() {
        return "index";
    }

    @RequestMapping(value = "shorten", method = RequestMethod.POST)
    String shorten(@Validated ShortenForm form, BindingResult result, RedirectAttributes attributes) throws IOException {
        if (result.hasErrors()) {
            return "index";
        }
        String shortenUrl = remoteCommand.shorten(form.getUrl());
        attributes.addFlashAttribute("shortenUrl", shortenUrl);
        return "redirect:/";
    }

    @RequestMapping(value = "{hash}", method = RequestMethod.GET)
    String redirect(@PathVariable String hash) {
        String url = remoteCommand.getUrl(hash);
        return "redirect:" + url;
    }
}

class ShortenForm {
    @NotEmpty
    @URL
    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}

@Component
@RefreshScope
class RemoteCommand {
    @Autowired
    RestTemplate restTemplate;
    @Value("${urlshorten.api.url:http://localhost:8081}")
    String apiUrl;
    Logger logger = LoggerFactory.getLogger(RemoteCommand.class);

    @HystrixCommand(fallbackMethod = "defaultShorten")
    public String shorten(String url) {
        logger.info("calling shorten {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("url", url);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(apiUrl, request, String.class).getBody();
    }

    public String defaultShorten(String url) {
        logger.info("failed shorten {}", url);
        return "failed!";
    }

    @HystrixCommand(fallbackMethod = "defaultGetUrl")
    public String getUrl(String hash) {
        logger.info("calling getUrl {}", hash);
        return restTemplate.getForObject(apiUrl + "/{hash}", String.class, singletonMap("hash", hash));
    }

    public String defaultGetUrl(String hash) {
        logger.info("failed getUrl {}", hash);
        return "failed!";
    }
}